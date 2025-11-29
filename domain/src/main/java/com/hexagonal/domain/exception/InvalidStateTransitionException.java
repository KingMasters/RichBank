package com.hexagonal.domain.exception;

public class InvalidStateTransitionException extends DomainException {
    public InvalidStateTransitionException(String currentState, String targetState) {
        super(String.format("Cannot transition from %s to %s", currentState, targetState));
    }

    public InvalidStateTransitionException(String message) {
        super(message);
    }
}

