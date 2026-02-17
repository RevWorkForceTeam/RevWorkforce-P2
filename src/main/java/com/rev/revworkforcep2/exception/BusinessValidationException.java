package com.rev.revworkforcep2.exception;

public class BusinessValidationException extends ApplicationException {

    public BusinessValidationException(String message) {
        super(message, 400);
    }
}
