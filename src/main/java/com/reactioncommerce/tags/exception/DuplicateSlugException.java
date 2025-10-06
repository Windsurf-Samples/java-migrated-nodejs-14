package com.reactioncommerce.tags.exception;

public class DuplicateSlugException extends RuntimeException {
    public DuplicateSlugException(String slug) {
        super("Slug " + slug + " is already in use");
    }
}
