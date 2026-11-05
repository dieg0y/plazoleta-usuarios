package com.plazoleta.usuario.infrastructure.input.rest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.plazoleta.usuario.application.port.input.ObtenerUsuarioUseCase;
import com.plazoleta.usuario.domain.model.Rol;
import com.plazoleta.usuario.infrastructure.input.rest.dto.CelularClienteResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Integración interna", description = "Datos mínimos de contacto para otros microservicios")
@RestController
@RequestMapping("/internal/usuarios")
public class UsuarioInternoController {

    private static final String INTERNAL_TOKEN_HEADER = "X-Internal-Token";

    private final ObtenerUsuarioUseCase obtenerUsuarioUseCase;
    private final String internalToken;

    public UsuarioInternoController(
            ObtenerUsuarioUseCase obtenerUsuarioUseCase,
            @Value("${app.internal.token:}") String internalToken) {
        this.obtenerUsuarioUseCase = obtenerUsuarioUseCase;
        this.internalToken = internalToken;
    }

    @Operation(summary = "Obtener el celular de un cliente para enviarle una notificación")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Celular del cliente"),
            @ApiResponse(responseCode = "401", description = "Credencial interna inválida"),
            @ApiResponse(responseCode = "400", description = "El usuario solicitado no es un cliente")
    })
    @GetMapping("/{id}/celular")
    public ResponseEntity<CelularClienteResponse> obtenerCelularCliente(
            @PathVariable Long id,
            @RequestHeader(name = INTERNAL_TOKEN_HEADER, required = false) String token) {
        if (!tokenValido(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credencial interna inválida");
        }

        var usuario = obtenerUsuarioUseCase.obtenerUsuarioPorId(id);
        if (usuario.getRol() != Rol.CLIENTE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario solicitado no es un cliente");
        }
        return ResponseEntity.ok(new CelularClienteResponse(usuario.getCelular()));
    }

    private boolean tokenValido(String token) {
        return internalToken != null
                && internalToken.length() >= 32
                && token != null
                && MessageDigest.isEqual(
                        internalToken.getBytes(StandardCharsets.UTF_8),
                        token.getBytes(StandardCharsets.UTF_8));
    }
}
