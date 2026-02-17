package com.rev.revworkforcep2.exception;

public class ResourceNotFoundException extends ApplicationException {

    public ResourceNotFoundException(String message) {
        super(message, 404);
    }
}
