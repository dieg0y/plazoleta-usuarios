package com.plazoleta.usuario.application.port.input;

import com.plazoleta.usuario.domain.model.Usuario;

public interface ObtenerUsuarioUseCase {

    Usuario obtenerUsuarioPorId(Long id);
}