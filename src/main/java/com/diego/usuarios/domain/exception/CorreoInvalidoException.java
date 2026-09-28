package com.diego.usuarios.domain.exception;

public class CorreoInvalidoException extends RuntimeException {
    public CorreoInvalidoException(String message) {
        super(message);
    }
}