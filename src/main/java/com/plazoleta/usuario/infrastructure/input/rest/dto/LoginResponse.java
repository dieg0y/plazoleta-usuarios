package com.plazoleta.usuario.infrastructure.input.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Token JWT emitido al iniciar sesion")
public class LoginResponse {

    @Schema(description = "Token que se envia como Bearer en endpoints protegidos")
    private String token;
}