package com.hexagonal.domain.exception;

import com.hexagonal.domain.vo.ID;

public class EntityNotFoundException extends DomainException {
    public EntityNotFoundException(String entityName, ID id) {
        super(String.format("%s with ID %s not found", entityName, id));
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}

