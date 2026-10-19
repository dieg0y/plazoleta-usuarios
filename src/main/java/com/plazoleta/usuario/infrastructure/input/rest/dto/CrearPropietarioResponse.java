package com.plazoleta.usuario.infrastructure.input.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resultado del registro de un propietario")
public class CrearPropietarioResponse {

    @Schema(description = "Identificador de la cuenta")
    private Long id;
    @Schema(description = "Mensaje del resultado")
    private String mensaje;
}