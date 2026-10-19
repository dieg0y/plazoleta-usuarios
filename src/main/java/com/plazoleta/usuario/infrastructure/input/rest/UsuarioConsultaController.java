package com.plazoleta.usuario.infrastructure.input.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.plazoleta.usuario.application.port.input.ObtenerUsuarioUseCase;
import com.plazoleta.usuario.domain.model.Usuario;
import com.plazoleta.usuario.infrastructure.input.rest.dto.UsuarioResponse;
import com.plazoleta.usuario.infrastructure.input.rest.mapper.UsuarioRestMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

@Tag(name = "Usuarios", description = "Consulta de usuarios del sistema")
@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioConsultaController {

    private final ObtenerUsuarioUseCase obtenerUsuarioUseCase;
    private final UsuarioRestMapper usuarioRestMapper;

    @Operation(summary = "Obtener un usuario por su id",
               description = "Devuelve un resumen mínimo de usuario para consumidores internos, sin exponer datos sensibles.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "400", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtenerUsuarioPorId(@PathVariable Long id) {
        Usuario usuario = obtenerUsuarioUseCase.obtenerUsuarioPorId(id);
        UsuarioResponse response = usuarioRestMapper.toUsuarioResponse(usuario);
        response.setDocumentoIdentidad(null);
        response.setCelular(null);
        response.setFechaNacimiento(null);
        return ResponseEntity.ok(response);
    }
}