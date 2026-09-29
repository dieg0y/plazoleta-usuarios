package com.diego.usuarios.domain.exception;

public class CorreoDuplicadoException extends RuntimeException {
    public CorreoDuplicadoException(String message) {
        super(message);
    }
}
