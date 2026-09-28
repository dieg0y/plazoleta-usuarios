package com.diego.usuarios.infrastructure.output.jpa.adapter;

import com.diego.usuarios.domain.model.Usuario;
import com.diego.usuarios.domain.spi.IUsuarioPersistencePort;
import com.diego.usuarios.infrastructure.output.jpa.entity.UsuarioEntity;
import com.diego.usuarios.infrastructure.output.jpa.mapper.IUsuarioEntityMapper;
import com.diego.usuarios.infrastructure.output.jpa.repository.IUsuarioRepository;

public class UsuarioMysqlAdapter implements IUsuarioPersistencePort {

    private final IUsuarioRepository usuarioRepository;
    private final IUsuarioEntityMapper usuarioEntityMapper;

    public UsuarioMysqlAdapter(IUsuarioRepository usuarioRepository, IUsuarioEntityMapper usuarioEntityMapper) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioEntityMapper = usuarioEntityMapper;
    }

    @Override
    public void guardarUsuario(Usuario usuario) {
        UsuarioEntity entity = usuarioEntityMapper.toEntity(usuario);
        usuarioRepository.save(entity);
    }

    @Override
    public boolean existePorCorreo(String correo) {
        return usuarioRepository.existsByCorreo(correo);
    }
}