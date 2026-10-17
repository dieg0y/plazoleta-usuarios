package com.plazoleta.usuario.application.port.input;

import com.plazoleta.usuario.domain.model.Usuario;

/**
 * PUERTO DE ENTRADA (driving).
 * Lo que el mundo exterior PUEDE pedirle a la aplicación:
 * "crear un propietario".
 *
 * Lo implementa CrearPropietarioService (application/service)
 * y lo consume UsuarioController (infrastructure/input/rest).
 */
public interface CrearPropietarioUseCase {

    Usuario crearPropietario(Usuario usuario);
}