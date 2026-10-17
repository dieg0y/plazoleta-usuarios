package com.plazoleta.usuario.infrastructure.output.persistence.adapter;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.plazoleta.usuario.application.port.output.UsuarioRepositoryPort;
import com.plazoleta.usuario.domain.model.Usuario;
import com.plazoleta.usuario.infrastructure.output.persistence.entity.UsuarioEntity;
import com.plazoleta.usuario.infrastructure.output.persistence.mapper.UsuarioPersistenceMapper;
import com.plazoleta.usuario.infrastructure.output.persistence.repository.UsuarioJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UsuarioPersistenceAdapter implements UsuarioRepositoryPort {

    private final UsuarioJpaRepository usuarioJpaRepository;
    private final UsuarioPersistenceMapper usuarioPersistenceMapper;

    @Override
    public Usuario guardar(Usuario usuario) {
        UsuarioEntity entity = usuarioPersistenceMapper.toEntity(usuario);
        UsuarioEntity guardada = usuarioJpaRepository.save(entity);
        return usuarioPersistenceMapper.toDomain(guardada);
    }

    @Override
    public boolean existePorCorreo(String correo) {
        return usuarioJpaRepository.existsByCorreo(correo);
    }

    @Override
    public boolean existePorDocumento(String documentoIdentidad) {
        return usuarioJpaRepository.existsByDocumentoIdentidad(documentoIdentidad);
    }

    @Override
    public Optional<Usuario> obtenerPorId(Long id) {
        return usuarioJpaRepository.findById(id)
                .map(usuarioPersistenceMapper::toDomain);
    }
}