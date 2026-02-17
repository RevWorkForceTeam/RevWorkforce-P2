package com.rev.revworkforcep2.exception;

public class ConflictException extends ApplicationException {

    public ConflictException(String message) {
        super(message, 409);
    }
}
