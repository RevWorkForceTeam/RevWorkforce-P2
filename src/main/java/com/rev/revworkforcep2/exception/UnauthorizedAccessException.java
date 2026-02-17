package com.rev.revworkforcep2.exception;

public class UnauthorizedAccessException extends ApplicationException {

    public UnauthorizedAccessException(String message) {
        super(message, 401);
    }
}
