package com.plazoleta.usuario.application.port.output;

import java.util.Optional;

import com.plazoleta.usuario.domain.model.Usuario;

public interface UsuarioRepositoryPort {

    Usuario guardar(Usuario usuario);

    boolean existePorCorreo(String correo);

    boolean existePorDocumento(String documentoIdentidad);

    Optional<Usuario> obtenerPorId(Long id);

    /** HU5: busca un usuario por correo para el login. */
    java.util.Optional<Usuario> obtenerPorCorreo(String correo);
}