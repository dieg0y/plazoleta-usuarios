package com.diego.usuarios.domain.api;

import com.diego.usuarios.domain.model.Usuario;

public interface IUsuarioServicePort {
    void guardarPropietario(Usuario usuario);
    void guardarEmpleado(Usuario usuario, Long restauranteId, Long propietarioId, String authorization);
    void guardarCliente(Usuario usuario);
    Usuario autenticar(String correo, String clave);
    boolean usuarioTieneRol(Long usuarioId, String rol);
}