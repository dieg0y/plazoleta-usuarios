package com.diego.usuarios.domain.api;

import com.diego.usuarios.domain.model.Usuario;

public interface IUsuarioServicePort {
    void guardarPropietario(Usuario usuario);
}