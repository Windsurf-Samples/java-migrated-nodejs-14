package com.reactioncommerce.tags.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagConnection {
    private List<TagEdge> edges;
    private List<Tag> nodes;
    private PageInfo pageInfo;
    private Integer totalCount;
}
