package com.reactioncommerce.tags.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Tags")
@CompoundIndex(name = "shopId_slug_unique", def = "{'shopId': 1, 'slug': 1}", unique = true)
public class Tag {
    @Id
    @JsonProperty("_id")
    private String id;
    
    public String get_id() {
        return id;
    }
    
    public void set_id(String id) {
        this.id = id;
    }
    
    @Indexed
    private String name;
    
    @Indexed
    private String slug;
    
    private String displayTitle;
    private String heroMediaUrl;
    
    @Builder.Default
    private Boolean isVisible = true;
    
    @Builder.Default
    private Boolean isDeleted = false;
    
    @Builder.Default
    private Boolean isTopLevel = false;
    
    private Integer position;
    
    @Indexed
    private String shopId;
    
    private List<Metafield> metafields;
    private List<String> featuredProductIds;
    private List<String> relatedTagIds;
    private List<String> subTagIds;
    
    @CreatedDate
    private Instant createdAt;
    
    @LastModifiedDate
    private Instant updatedAt;
}
