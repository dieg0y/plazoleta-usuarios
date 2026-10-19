package com.plazoleta.usuario.application.service;

import com.plazoleta.usuario.application.port.input.CrearClienteUseCase;
import com.plazoleta.usuario.application.port.output.PasswordEncoderPort;
import com.plazoleta.usuario.application.port.output.UsuarioRepositoryPort;
import com.plazoleta.usuario.domain.model.Rol;
import com.plazoleta.usuario.domain.model.Usuario;

/**
 * CASO DE USO HU8: crear la cuenta de un cliente.
 * Auto-registro público con validaciones de formato y unicidad; la fecha de
 * nacimiento es opcional y el rol queda forzado a CLIENTE.
 */
public class CrearClienteService implements CrearClienteUseCase {

    private final ValidadorUsuario validadorUsuario;
    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;

    public CrearClienteService(ValidadorUsuario validadorUsuario,
                               UsuarioRepositoryPort usuarioRepositoryPort,
                               PasswordEncoderPort passwordEncoderPort) {
        this.validadorUsuario = validadorUsuario;
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    @Override
    public Usuario crearCliente(Usuario usuario) {
        validadorUsuario.validarNuevoUsuario(usuario, false);

        Usuario cliente = Usuario.builder()
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .documentoIdentidad(usuario.getDocumentoIdentidad())
                .celular(usuario.getCelular())
                .fechaNacimiento(usuario.getFechaNacimiento())
                .correo(usuario.getCorreo())
                .clave(passwordEncoderPort.encode(usuario.getClave()))
                .rol(Rol.CLIENTE)
                .build();

        return usuarioRepositoryPort.guardar(cliente);
    }
}