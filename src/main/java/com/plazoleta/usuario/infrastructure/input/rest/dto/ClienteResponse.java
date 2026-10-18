package com.plazoleta.usuario.infrastructure.input.rest.dto;

import com.plazoleta.usuario.domain.model.Rol;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Cuenta de cliente creada")
public class ClienteResponse {

    @Schema(description = "Id de la cuenta creada", example = "6")
    private Long id;

    @Schema(description = "Nombre del cliente", example = "Laura")
    private String nombre;

    @Schema(description = "Apellido del cliente", example = "Martinez")
    private String apellido;

    @Schema(description = "Correo electrónico", example = "laura.cliente@correo.com")
    private String correo;

    @Schema(description = "Rol asignado", example = "CLIENTE")
    private Rol rol;
}