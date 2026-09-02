package com.abhinay.buildrix_ai.exceptions;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException{
    private final String message;

    public BadRequestException(String message) {
        this.message = message;
        super(message);
    }
}
