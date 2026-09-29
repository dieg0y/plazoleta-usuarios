package com.diego.usuarios.domain.spi;

import com.diego.usuarios.domain.model.Usuario;

import java.util.Optional;

public interface IUsuarioPersistencePort {
    void guardarUsuario(Usuario usuario);
    boolean existePorCorreo(String correo);
    boolean existePorDocumento(String documentoIdentidad);
    Optional<Usuario> buscarPorCorreo(String correo);
    Optional<Usuario> buscarPorId(Long id);
}