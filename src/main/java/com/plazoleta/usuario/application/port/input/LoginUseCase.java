package com.plazoleta.usuario.application.port.input;

/**
 * PUERTO DE ENTRADA HU5: inicio de sesión.
 * Devuelve el JWT si las credenciales son correctas.
 */
public interface LoginUseCase {

    String login(String correo, String clave);
}