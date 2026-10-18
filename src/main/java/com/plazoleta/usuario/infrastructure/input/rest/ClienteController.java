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

import com.plazoleta.usuario.application.port.input.CrearClienteUseCase;
import com.plazoleta.usuario.domain.model.Usuario;
import com.plazoleta.usuario.infrastructure.input.rest.dto.ClienteResponse;
import com.plazoleta.usuario.infrastructure.input.rest.dto.CrearClienteRequest;
import com.plazoleta.usuario.infrastructure.input.rest.mapper.UsuarioRestMapper;

import lombok.RequiredArgsConstructor;

@Tag(name = "Clientes", description = "Cuentas de clientes con auto-registro público (HU8)")
@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final CrearClienteUseCase crearClienteUseCase;
    private final UsuarioRestMapper usuarioRestMapper;

    @Operation(summary = "Crear la cuenta de un cliente (auto-registro)",
            description = "Registro público (sin autenticación) con las mismas validaciones "
                        + "de HU1 (formatos, mayoría de edad, unicidad) y el rol forzado a CLIENTE. "
                        + "La clave se almacena encriptada con BCrypt. Con la cuenta creada, "
                        + "el cliente ya puede autenticarse en POST /login.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o cuenta ya existente")
    })
    @PostMapping
    public ResponseEntity<ClienteResponse> crearCliente(
            @RequestBody CrearClienteRequest request) {

        Usuario aCrear = usuarioRestMapper.toDomain(request);
        Usuario creado = crearClienteUseCase.crearCliente(aCrear);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuarioRestMapper.toClienteResponse(creado));
    }
}