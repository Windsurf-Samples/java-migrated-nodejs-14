package com.reactioncommerce.tags.controller;

import com.reactioncommerce.tags.model.Metafield;
import com.reactioncommerce.tags.model.Tag;
import com.reactioncommerce.tags.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;
    
    @QueryMapping
    public Mono<Tag> tag(
        @Argument String slugOrId,
        @Argument String shopId,
        @Argument(name = "shouldIncludeInvisible") Boolean includeInvisible
    ) {
        return tagService.getTag(slugOrId, shopId, 
            includeInvisible != null && includeInvisible);
    }
    
    @QueryMapping
    public Mono<Map<String, Object>> tags(
        @Argument String shopId,
        @Argument Boolean isTopLevel,
        @Argument Boolean shouldIncludeDeleted,
        @Argument Boolean shouldIncludeInvisible,
        @Argument String sortBy,
        @Argument String sortOrder
    ) {
        Flux<Tag> tagFlux = tagService.getTags(
            shopId,
            isTopLevel,
            shouldIncludeDeleted != null && shouldIncludeDeleted,
            shouldIncludeInvisible != null && shouldIncludeInvisible,
            sortBy,
            "desc".equals(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC
        );
        
        return tagFlux.collectList()
            .map(tags -> Map.of(
                "nodes", tags,
                "totalCount", tags.size(),
                "pageInfo", Map.of(
                    "hasNextPage", false,
                    "hasPreviousPage", false,
                    "startCursor", null,
                    "endCursor", null
                )
            ));
    }
    
    @QueryMapping
    public Mono<Map<String, Object>> productsByTagId(
        @Argument String shopId,
        @Argument String tagId
    ) {
        return Mono.just(Map.of(
            "nodes", Collections.emptyList(),
            "totalCount", 0,
            "pageInfo", Map.of(
                "hasNextPage", false,
                "hasPreviousPage", false,
                "startCursor", null,
                "endCursor", null
            )
        ));
    }
    
    @MutationMapping
    public Mono<Map<String, Object>> addTag(@Argument Map<String, Object> input) {
        String clientMutationId = (String) input.get("clientMutationId");
        String shopId = (String) input.get("shopId");
        
        Tag tag = Tag.builder()
            .name((String) input.get("name"))
            .slug((String) input.get("slug"))
            .displayTitle((String) input.get("displayTitle"))
            .heroMediaUrl((String) input.get("heroMediaUrl"))
            .isVisible((Boolean) input.get("isVisible"))
            .metafields(convertMetafields((List<?>) input.get("metafields")))
            .shopId(shopId)
            .isDeleted(false)
            .isTopLevel(false)
            .build();
        
        return tagService.createTag(tag)
            .map(createdTag -> Map.of(
                "clientMutationId", clientMutationId != null ? clientMutationId : "",
                "shopId", shopId,
                "tag", createdTag
            ));
    }
    
    @MutationMapping
    public Mono<Map<String, Object>> updateTag(@Argument Map<String, Object> input) {
        String clientMutationId = (String) input.get("clientMutationId");
        String tagId = (String) input.get("id");
        String shopId = (String) input.get("shopId");
        
        Tag updates = Tag.builder()
            .name((String) input.get("name"))
            .slug((String) input.get("slug"))
            .displayTitle((String) input.get("displayTitle"))
            .heroMediaUrl((String) input.get("heroMediaUrl"))
            .isVisible((Boolean) input.get("isVisible"))
            .metafields(convertMetafields((List<?>) input.get("metafields")))
            .featuredProductIds((List<String>) input.get("featuredProductIds"))
            .build();
        
        return tagService.updateTag(tagId, shopId, updates)
            .map(updatedTag -> Map.of(
                "clientMutationId", clientMutationId != null ? clientMutationId : "",
                "tag", updatedTag
            ));
    }
    
    private List<Metafield> convertMetafields(List<?> metafieldsInput) {
        if (metafieldsInput == null) {
            return null;
        }
        
        return metafieldsInput.stream()
            .map(m -> {
                Map<String, String> map = (Map<String, String>) m;
                return Metafield.builder()
                    .key(map.get("key"))
                    .namespace(map.get("namespace"))
                    .scope(map.get("scope"))
                    .value(map.get("value"))
                    .valueType(map.get("valueType"))
                    .description(map.get("description"))
                    .build();
            })
            .toList();
    }
}
