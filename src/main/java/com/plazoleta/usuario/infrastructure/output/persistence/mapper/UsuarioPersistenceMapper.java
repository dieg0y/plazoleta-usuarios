package com.plazoleta.usuario.infrastructure.output.persistence.mapper;

import com.plazoleta.usuario.domain.model.Usuario;
import com.plazoleta.usuario.infrastructure.output.persistence.entity.UsuarioEntity;
import org.mapstruct.Mapper;

/**
 * Traductor entre el mundo MySQL (UsuarioEntity) y el mundo del
 * negocio (Usuario del dominio). MapStruct genera la implementación
 * (UsuarioPersistenceMapperImpl) en tiempo de compilación.
 *
 * NOTA DE DISEÑO: no usamos componentModel = "spring" porque el
 * component scanning de Spring Framework 7 ignora las clases
 * generadas (@Generated). La instancia se registra EXPLICITAMENTE
 * como @Bean en BeanConfiguration (el composition root).
 */
@Mapper(componentModel = "spring")
public interface UsuarioPersistenceMapper {

    UsuarioEntity toEntity(Usuario usuario);
    Usuario toDomain(UsuarioEntity entity);
}