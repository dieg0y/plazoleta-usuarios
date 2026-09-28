package com.diego.usuarios.infrastructure.input.rest;

import com.diego.usuarios.domain.api.IUsuarioServicePort;
import com.diego.usuarios.infrastructure.input.rest.dto.UsuarioRequestDto;
import com.diego.usuarios.infrastructure.input.rest.mapper.IUsuarioRequestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioRestController {

    private final IUsuarioServicePort usuarioServicePort;
    private final IUsuarioRequestMapper usuarioRequestMapper;

    public UsuarioRestController(IUsuarioServicePort usuarioServicePort, IUsuarioRequestMapper usuarioRequestMapper) {
        this.usuarioServicePort = usuarioServicePort;
        this.usuarioRequestMapper = usuarioRequestMapper;
    }

    @Operation(summary = "Crear un nuevo propietario de restaurante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Propietario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o faltantes")
    })
    @PostMapping("/propietario")
    public ResponseEntity<Void> crearPropietario(@RequestBody UsuarioRequestDto dto) {
        usuarioServicePort.guardarPropietario(usuarioRequestMapper.toDomain(dto));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}