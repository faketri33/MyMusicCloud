package com.mmc.auth.domain.exceptions;

public class InvalidJwt extends RuntimeException {
    public InvalidJwt(String message) {
        super(message);
    }
}
