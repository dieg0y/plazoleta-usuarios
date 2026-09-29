package com.diego.usuarios.domain.exception;

public class RestauranteNoAutorizadoException extends RuntimeException {
    public RestauranteNoAutorizadoException() {
        super("El propietario no tiene acceso al restaurante indicado");
    }
}
