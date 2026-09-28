package com.diego.usuarios.infrastructure.output.jpa.mapper;

import com.diego.usuarios.domain.model.Usuario;
import com.diego.usuarios.infrastructure.output.jpa.entity.UsuarioEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IUsuarioEntityMapper {
    UsuarioEntity toEntity(Usuario usuario);
    Usuario toDomain(UsuarioEntity usuarioEntity);
}