package com.plazoleta.usuario.infrastructure.input.rest.dto;

import java.time.LocalDate;

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
@Schema(description = "Datos para crear una cuenta de propietario (HU1)")
public class CrearPropietarioRequest {

    @Schema(description = "Nombre", example = "Juan", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;
    @Schema(description = "Apellido", example = "Perez", requiredMode = Schema.RequiredMode.REQUIRED)
    private String apellido;
    @Schema(description = "Documento numerico", example = "1000000002", requiredMode = Schema.RequiredMode.REQUIRED)
    private String documentoIdentidad;
    @Schema(description = "Celular de hasta 13 caracteres", example = "+573000000002", requiredMode = Schema.RequiredMode.REQUIRED)
    private String celular;
    @Schema(description = "Fecha de nacimiento; el titular debe ser mayor de edad",
            example = "1990-01-01", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate fechaNacimiento;
    @Schema(description = "Correo electronico", example = "juan@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String correo;
    @Schema(description = "Clave de acceso; se almacena con BCrypt", example = "Propietario123*",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String clave;
}