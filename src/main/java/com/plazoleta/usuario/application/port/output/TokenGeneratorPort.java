package com.plazoleta.usuario.application.port.output;

import com.plazoleta.usuario.domain.model.Usuario;

/**
 * PUERTO DE SALIDA HU5: generación del token de sesión (JWT).
 * La aplicación no sabe JJWT: solo pide "un token para este usuario".
 */
public interface TokenGeneratorPort {

    String generar(Usuario usuario);
}