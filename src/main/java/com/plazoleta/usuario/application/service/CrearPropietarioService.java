package com.plazoleta.usuario.application.service;

import com.plazoleta.usuario.application.port.input.CrearPropietarioUseCase;
import com.plazoleta.usuario.application.port.output.PasswordEncoderPort;
import com.plazoleta.usuario.application.port.output.UsuarioRepositoryPort;
import com.plazoleta.usuario.domain.model.Rol;
import com.plazoleta.usuario.domain.model.Usuario;

/**
 * CASO DE USO HU1: crear la cuenta de un propietario.
 * Las validaciones comunes viven en ValidadorUsuario;
 * aquí solo queda lo específico: el rol PROPIETARIO.
 */
public class CrearPropietarioService implements CrearPropietarioUseCase {

    private final ValidadorUsuario validadorUsuario;
    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;

    public CrearPropietarioService(ValidadorUsuario validadorUsuario,
                                   UsuarioRepositoryPort usuarioRepositoryPort,
                                   PasswordEncoderPort passwordEncoderPort) {
        this.validadorUsuario = validadorUsuario;
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    @Override
    public Usuario crearPropietario(Usuario usuario) {
        validadorUsuario.validarNuevoUsuario(usuario);

        Usuario propietario = Usuario.builder()
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .documentoIdentidad(usuario.getDocumentoIdentidad())
                .celular(usuario.getCelular())
                .fechaNacimiento(usuario.getFechaNacimiento())
                .correo(usuario.getCorreo())
                .clave(passwordEncoderPort.encode(usuario.getClave()))
                .rol(Rol.PROPIETARIO)
                .build();

        return usuarioRepositoryPort.guardar(propietario);
    }
}