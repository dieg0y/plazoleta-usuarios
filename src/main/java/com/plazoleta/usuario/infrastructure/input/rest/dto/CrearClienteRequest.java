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
@Schema(description = "Datos para crear la cuenta de un cliente (HU8)")
public class CrearClienteRequest {

    @Schema(description = "Nombre del cliente", example = "Laura")
    private String nombre;

    @Schema(description = "Apellido del cliente", example = "Martinez")
    private String apellido;

    @Schema(description = "Documento de identidad (solo números)", example = "1122334455")
    private String documentoIdentidad;

    @Schema(description = "Celular (solo números, + opcional, máx 13)", example = "+573001234567")
    private String celular;

    @Schema(description = "Fecha de nacimiento opcional (YYYY-MM-DD)", example = "1995-07-20")
    private LocalDate fechaNacimiento;

    @Schema(description = "Correo electrónico (usuario de acceso)", example = "laura.cliente@correo.com")
    private String correo;

    @Schema(description = "Clave de acceso", example = "claveCliente123")
    private String clave;
}