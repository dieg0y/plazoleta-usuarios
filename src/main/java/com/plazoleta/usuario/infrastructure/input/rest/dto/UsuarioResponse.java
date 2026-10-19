package com.plazoleta.usuario.infrastructure.input.rest.dto;

import java.time.LocalDate;

import com.plazoleta.usuario.domain.model.Rol;

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
@Schema(description = "Datos de respuesta de usuario")
public class UsuarioResponse {

    @Schema(description = "Identificador del usuario")
    private Long id;
    @Schema(description = "Nombre")
    private String nombre;
    @Schema(description = "Apellido")
    private String apellido;
    @Schema(description = "Documento de identidad")
    private String documentoIdentidad;
    @Schema(description = "Celular")
    private String celular;
    @Schema(description = "Fecha de nacimiento")
    private LocalDate fechaNacimiento;
    @Schema(description = "Correo electronico")
    private String correo;
    @Schema(description = "Rol asignado")
    private Rol rol;
}