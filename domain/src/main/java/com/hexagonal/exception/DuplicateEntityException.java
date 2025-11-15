package com.hexagonal.exception;

public class DuplicateEntityException extends DomainException {
    public DuplicateEntityException(String entityName, String field, String value) {
        super(String.format("%s with %s '%s' already exists", entityName, field, value));
    }

    public DuplicateEntityException(String message) {
        super(message);
    }
}

