package com.plazoleta.usuario.application.service;

import com.plazoleta.usuario.application.port.input.CrearEmpleadoUseCase;
import com.plazoleta.usuario.application.port.output.PasswordEncoderPort;
import com.plazoleta.usuario.application.port.output.UsuarioRepositoryPort;
import com.plazoleta.usuario.domain.model.Rol;
import com.plazoleta.usuario.domain.model.Usuario;

/**
 * CASO DE USO HU4: crear la cuenta de un empleado.
 * Valida formatos y unicidad; la fecha de nacimiento es opcional y el rol
 * queda forzado a EMPLEADO.
 */
public class CrearEmpleadoService implements CrearEmpleadoUseCase {

    private final ValidadorUsuario validadorUsuario;
    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;

    public CrearEmpleadoService(ValidadorUsuario validadorUsuario,
                                UsuarioRepositoryPort usuarioRepositoryPort,
                                PasswordEncoderPort passwordEncoderPort) {
        this.validadorUsuario = validadorUsuario;
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    @Override
    public Usuario crearEmpleado(Usuario usuario) {
        validadorUsuario.validarNuevoUsuario(usuario, false);

        Usuario empleado = Usuario.builder()
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .documentoIdentidad(usuario.getDocumentoIdentidad())
                .celular(usuario.getCelular())
                .fechaNacimiento(usuario.getFechaNacimiento())
                .correo(usuario.getCorreo())
                .clave(passwordEncoderPort.encode(usuario.getClave()))
                .rol(Rol.EMPLEADO)
                .build();

        return usuarioRepositoryPort.guardar(empleado);
    }
}