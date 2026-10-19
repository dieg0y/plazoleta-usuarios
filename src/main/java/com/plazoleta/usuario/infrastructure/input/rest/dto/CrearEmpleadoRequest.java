package com.plazoleta.usuario.infrastructure.input.rest.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para crear la cuenta de un empleado (HU4)")
public class CrearEmpleadoRequest {

    @Schema(description = "Nombre del empleado", example = "Ana")
    private String nombre;

    @Schema(description = "Apellido del empleado", example = "Gomez")
    private String apellido;

    @Schema(description = "Documento de identidad (solo números)", example = "987654321")
    private String documentoIdentidad;

    @Schema(description = "Celular (solo números, + opcional, máx 13)", example = "+573009876543")
    private String celular;

    @Schema(description = "Fecha de nacimiento opcional (YYYY-MM-DD)", example = "1998-03-10")
    private LocalDate fechaNacimiento;

    @Schema(description = "Correo electrónico (usuario de acceso)", example = "ana.empleado@correo.com")
    private String correo;

    @Schema(description = "Clave de acceso", example = "claveSegura123")
    private String clave;
}