package com.diego.usuarios.infrastructure.input.rest;

import com.diego.usuarios.domain.api.IUsuarioServicePort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/usuarios")
public class UsuarioInternalRestController {

    private final IUsuarioServicePort usuarioServicePort;

    public UsuarioInternalRestController(IUsuarioServicePort usuarioServicePort) {
        this.usuarioServicePort = usuarioServicePort;
    }

    @GetMapping("/{usuarioId}/roles/{rol}")
    @Operation(summary = "Validar el rol de una cuenta para servicios internos",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Boolean> validarRol(@PathVariable Long usuarioId, @PathVariable String rol) {
        return ResponseEntity.ok(usuarioServicePort.usuarioTieneRol(usuarioId, rol));
    }
}
