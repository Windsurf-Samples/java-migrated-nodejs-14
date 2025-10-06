package com.reactioncommerce.tags.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Metafield {
    private String key;
    private String namespace;
    private String scope;
    private String value;
    private String valueType;
    private String description;
}
