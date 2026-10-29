package com.bank.auth.exception;

/** Thrown when credentials are invalid. */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}