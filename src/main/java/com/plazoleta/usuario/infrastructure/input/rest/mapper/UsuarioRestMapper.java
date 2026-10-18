package com.plazoleta.usuario.infrastructure.input.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.plazoleta.usuario.domain.model.Usuario;
import com.plazoleta.usuario.infrastructure.input.rest.dto.CrearEmpleadoRequest;
import com.plazoleta.usuario.infrastructure.input.rest.dto.CrearPropietarioRequest;
import com.plazoleta.usuario.infrastructure.input.rest.dto.CrearPropietarioResponse;
import com.plazoleta.usuario.infrastructure.input.rest.dto.EmpleadoResponse;
import com.plazoleta.usuario.infrastructure.input.rest.dto.UsuarioResponse;

@Mapper(componentModel = "spring")
public interface UsuarioRestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rol", ignore = true)
    Usuario toDomain(CrearPropietarioRequest request);

    @Mapping(target = "mensaje", constant = "Propietario creado exitosamente")
    CrearPropietarioResponse toResponse(Usuario usuario);

    UsuarioResponse toUsuarioResponse(Usuario usuario);

    // ===== HU4: empleados =====

    @Mapping(target = "rol", ignore = true)
    Usuario toDomain(CrearEmpleadoRequest request);

    EmpleadoResponse toEmpleadoResponse(Usuario usuario);
}