package com.plazoleta.usuario.domain.exception;

/**
 * Excepción base del dominio: se lanza cuando se viola
 * una regla de negocio (ej: "El usuario debe ser mayor de edad").
 *
 * El dominio solo dice QUÉ está mal (el mensaje).
 * La infraestructura decide cómo presentarlo al exterior:
 * en el paso 3.6 un @RestControllerAdvice la convertirá en HTTP 400.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String mensaje) {
        super(mensaje);
    }
}

