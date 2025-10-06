package com.reactioncommerce.tags.service;

import com.mongodb.DuplicateKeyException;
import com.reactioncommerce.tags.exception.DuplicateSlugException;
import com.reactioncommerce.tags.exception.TagNotFoundException;
import com.reactioncommerce.tags.model.Tag;
import com.reactioncommerce.tags.repository.TagRepository;
import com.reactioncommerce.tags.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;
    private final ReactiveMongoTemplate mongoTemplate;
    
    public Mono<Tag> createTag(Tag tag) {
        if (tag.getSlug() == null || tag.getSlug().trim().isEmpty()) {
            tag.setSlug(SlugUtils.generateSlug(tag.getName()));
        } else {
            tag.setSlug(SlugUtils.generateSlug(tag.getSlug()));
        }
        
        return tagRepository.save(tag)
            .onErrorMap(DuplicateKeyException.class, 
                e -> new DuplicateSlugException(tag.getSlug()));
    }
    
    public Mono<Tag> updateTag(String tagId, String shopId, Tag updates) {
        return tagRepository.findByIdAndShopId(tagId, shopId)
            .switchIfEmpty(Mono.error(new TagNotFoundException("Tag not found")))
            .flatMap(existingTag -> {
                if (updates.getSlug() == null || updates.getSlug().trim().isEmpty()) {
                    updates.setSlug(SlugUtils.generateSlug(updates.getName()));
                } else {
                    updates.setSlug(SlugUtils.generateSlug(updates.getSlug()));
                }
                
                existingTag.setName(updates.getName());
                existingTag.setSlug(updates.getSlug());
                existingTag.setDisplayTitle(updates.getDisplayTitle());
                existingTag.setHeroMediaUrl(updates.getHeroMediaUrl());
                existingTag.setIsVisible(updates.getIsVisible());
                existingTag.setMetafields(updates.getMetafields());
                existingTag.setFeaturedProductIds(updates.getFeaturedProductIds());
                
                return tagRepository.save(existingTag)
                    .onErrorMap(DuplicateKeyException.class,
                        e -> new DuplicateSlugException(updates.getSlug()));
            });
    }
    
    public Mono<Tag> getTag(String slugOrId, String shopId, boolean includeInvisible) {
        return tagRepository.findByIdAndShopId(slugOrId, shopId)
            .switchIfEmpty(tagRepository.findBySlugAndShopId(slugOrId, shopId))
            .switchIfEmpty(Mono.error(new TagNotFoundException("Tag not found")))
            .flatMap(tag -> {
                if (!tag.getIsVisible() && !includeInvisible) {
                    return Mono.error(new TagNotFoundException("Tag not found"));
                }
                return Mono.just(tag);
            });
    }
    
    public Flux<Tag> getTags(String shopId, Boolean isTopLevel, boolean includeDeleted, 
                             boolean includeInvisible, String sortBy, Sort.Direction sortOrder) {
        Query query = new Query();
        query.addCriteria(Criteria.where("shopId").is(shopId));
        
        if (isTopLevel != null) {
            query.addCriteria(Criteria.where("isTopLevel").is(isTopLevel));
        }
        
        if (!includeDeleted) {
            query.addCriteria(Criteria.where("isDeleted").is(false));
        }
        
        if (!includeInvisible) {
            query.addCriteria(Criteria.where("isVisible").is(true));
        }
        
        query.with(Sort.by(sortOrder, sortBy != null ? sortBy : "position"));
        
        return mongoTemplate.find(query, Tag.class);
    }
}
