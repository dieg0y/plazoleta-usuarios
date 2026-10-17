package com.plazoleta.usuario.application.port.output;

/**
 * PUERTO DE SALIDA (driven).
 * La aplicación necesita cifrar claves, pero el ALGORITMO
 * es un detalle de infraestructura, no una regla de negocio.
 *
 * HU1 exige BCrypt: el adaptador de infraestructura usará el
 * BCryptPasswordEncoder de Spring Security para implementarlo.
 * Si mañana el negocio pidiera Argon2, se cambia el adaptador
 * y este puerto no se toca.
 */
public interface PasswordEncoderPort {

    String encode(String clave);
}