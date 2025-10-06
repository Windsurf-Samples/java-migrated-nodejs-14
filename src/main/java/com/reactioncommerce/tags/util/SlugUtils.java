package com.reactioncommerce.tags.util;

import com.github.slugify.Slugify;

public class SlugUtils {
    private static final Slugify slugify = Slugify.builder()
        .lowerCase(true)
        .build();
    
    public static String generateSlug(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }
        return slugify.slugify(input);
    }
}
