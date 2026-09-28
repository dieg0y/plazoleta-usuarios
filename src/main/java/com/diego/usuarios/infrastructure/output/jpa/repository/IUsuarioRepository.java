package com.diego.usuarios.infrastructure.output.jpa.repository;

import com.diego.usuarios.infrastructure.output.jpa.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    boolean existsByCorreo(String correo);
}