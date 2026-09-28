package com.diego.usuarios.domain.exception;

public class CelularInvalidoException extends RuntimeException {
    public CelularInvalidoException(String message) {
        super(message);
    }
}