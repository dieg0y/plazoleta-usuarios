package com.plazoleta.usuario.application.service;

import lombok.RequiredArgsConstructor;

import com.plazoleta.usuario.application.port.input.ObtenerUsuarioUseCase;
import com.plazoleta.usuario.application.port.output.UsuarioRepositoryPort;
import com.plazoleta.usuario.domain.exception.BusinessException;
import com.plazoleta.usuario.domain.model.Usuario;

@RequiredArgsConstructor
public class ObtenerUsuarioService implements ObtenerUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;

    @Override
    public Usuario obtenerUsuarioPorId(Long id) {
        return usuarioRepositoryPort.obtenerPorId(id)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado con id: " + id));
    }
}