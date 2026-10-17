package com.plazoleta.usuario.infrastructure.input.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.plazoleta.usuario.application.port.input.CrearPropietarioUseCase;
import com.plazoleta.usuario.domain.model.Usuario;
import com.plazoleta.usuario.infrastructure.input.rest.dto.CrearPropietarioRequest;
import com.plazoleta.usuario.infrastructure.input.rest.dto.CrearPropietarioResponse;
import com.plazoleta.usuario.infrastructure.input.rest.mapper.UsuarioRestMapper;

import lombok.RequiredArgsConstructor;

@Tag(name = "Usuarios", description = "Operaciones sobre usuarios del sistema")
@RestController
@RequestMapping("/propietarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final CrearPropietarioUseCase crearPropietarioUseCase;
    private final UsuarioRestMapper usuarioRestMapper;

    
    @Operation(summary = "Crear un nuevo propietario",
           description = "Registra un propietario validando formato de correo, celular numérico, "
                       + "documento numérico, mayoría de edad y unicidad de correo/documento. "
                       + "La clave se almacena encriptada con BCrypt.")
    @ApiResponses(value = {
         @ApiResponse(responseCode = "201", description = "Propietario creado exitosamente"),
          @ApiResponse(responseCode = "400", description = "Datos inválidos o propietario ya existente")
    })  
    @PostMapping
    public ResponseEntity<CrearPropietarioResponse> crearPropietario(
            @RequestBody CrearPropietarioRequest request) {

        Usuario aCrear = usuarioRestMapper.toDomain(request);
        Usuario creado = crearPropietarioUseCase.crearPropietario(aCrear);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuarioRestMapper.toResponse(creado));
    }
}