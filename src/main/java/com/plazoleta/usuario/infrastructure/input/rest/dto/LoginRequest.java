package com.plazoleta.usuario.infrastructure.input.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Credenciales para obtener un JWT")
public class LoginRequest {

    @Schema(description = "Correo registrado", example = "admin@plazoleta.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String correo;
    @Schema(description = "Clave de acceso", example = "Admin123*", requiredMode = Schema.RequiredMode.REQUIRED)
    private String clave;
}