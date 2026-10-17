package com.plazoleta.usuario.infrastructure.output.persistence.repository;

import com.plazoleta.usuario.infrastructure.output.persistence.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA implementa esta interface SOLO en runtime:
 * existeByCorreo genera "SELECT ... WHERE correo = ?" automáticamente
 * a partir del nombre del método (derived queries).
 */
public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, Long> {

    boolean existsByCorreo(String correo);

    boolean existsByDocumentoIdentidad(String documentoIdentidad);
}