package com.diego.usuarios.domain.exception;

public class DependenciaNoDisponibleException extends RuntimeException {
    public DependenciaNoDisponibleException(String dependencia, Throwable cause) {
        super("El servicio dependiente no está disponible: " + dependencia, cause);
    }
}
