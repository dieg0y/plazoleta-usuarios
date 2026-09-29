package com.diego.usuarios.infrastructure.input.rest.mapper;

import com.diego.usuarios.domain.model.Usuario;
import com.diego.usuarios.infrastructure.input.rest.dto.EmpleadoRequestDto;
import com.diego.usuarios.infrastructure.input.rest.dto.RegistroUsuarioDto;
import com.diego.usuarios.infrastructure.input.rest.dto.UsuarioRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IUsuarioRequestMapper {
    Usuario toDomain(UsuarioRequestDto dto);
    Usuario toDomain(RegistroUsuarioDto dto);
    Usuario toDomain(EmpleadoRequestDto dto);
}