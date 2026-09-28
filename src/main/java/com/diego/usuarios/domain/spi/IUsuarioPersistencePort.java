package com.diego.usuarios.domain.spi;

import com.diego.usuarios.domain.model.Usuario;

public interface IUsuarioPersistencePort {
    void guardarUsuario(Usuario usuario);
    boolean existePorCorreo(String correo);
}