package com.plazoleta.usuario.infrastructure.input.rest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.plazoleta.usuario.application.port.input.LoginUseCase;
import com.plazoleta.usuario.infrastructure.input.rest.dto.LoginRequest;
import com.plazoleta.usuario.infrastructure.input.rest.dto.LoginResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

@Tag(name = "Autenticación", description = "Inicio de sesión y emisión de JWT (HU5)")
@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class AuthController {

    private final LoginUseCase loginUseCase;

    @Operation(summary = "Iniciar sesión",
            description = "Valida correo y clave (BCrypt) y devuelve un JWT firmado "
                        + "con el id, el rol y el correo del usuario. "
                        + "El token se envía en el header Authorization: Bearer <token>.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa"),
            @ApiResponse(responseCode = "400", description = "Credenciales inválidas o incompletas")
    })
    @PostMapping
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        String token = loginUseCase.login(request.getCorreo(), request.getClave());
        return ResponseEntity.ok(new LoginResponse(token));
    }
}