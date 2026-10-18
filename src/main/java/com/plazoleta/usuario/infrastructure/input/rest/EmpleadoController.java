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

import com.plazoleta.usuario.application.port.input.CrearEmpleadoUseCase;
import com.plazoleta.usuario.domain.model.Usuario;
import com.plazoleta.usuario.infrastructure.input.rest.dto.CrearEmpleadoRequest;
import com.plazoleta.usuario.infrastructure.input.rest.dto.EmpleadoResponse;
import com.plazoleta.usuario.infrastructure.input.rest.mapper.UsuarioRestMapper;

import lombok.RequiredArgsConstructor;

@Tag(name = "Empleados", description = "Cuentas de empleados de los restaurantes (HU4)")
@RestController
@RequestMapping("/empleados")
@RequiredArgsConstructor
public class EmpleadoController {

    private final CrearEmpleadoUseCase crearEmpleadoUseCase;
    private final UsuarioRestMapper usuarioRestMapper;

    @Operation(summary = "Crear la cuenta de un empleado",
            description = "Registra la cuenta de un empleado con las mismas validaciones "
                        + "de HU1 (formatos, mayoría de edad, unicidad) y el rol forzado a EMPLEADO. "
                        + "La clave se almacena encriptada con BCrypt.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Empleado creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o cuenta ya existente")
    })
    @PostMapping
    public ResponseEntity<EmpleadoResponse> crearEmpleado(
            @RequestBody CrearEmpleadoRequest request) {

        Usuario aCrear = usuarioRestMapper.toDomain(request);
        Usuario creado = crearEmpleadoUseCase.crearEmpleado(aCrear);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuarioRestMapper.toEmpleadoResponse(creado));
    }
}