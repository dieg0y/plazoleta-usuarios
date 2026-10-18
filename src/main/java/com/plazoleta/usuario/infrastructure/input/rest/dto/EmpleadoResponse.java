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
@Schema(description = "Cuenta de empleado creada")
public class EmpleadoResponse {

    @Schema(description = "Id de la cuenta creada", example = "5")
    private Long id;

    @Schema(description = "Nombre del empleado", example = "Ana")
    private String nombre;

    @Schema(description = "Apellido del empleado", example = "Gomez")
    private String apellido;

    @Schema(description = "Correo electrónico", example = "ana.empleado@correo.com")
    private String correo;

    @Schema(description = "Rol asignado", example = "EMPLEADO")
    private Rol rol;
}