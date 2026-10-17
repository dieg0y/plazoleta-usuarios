package com.plazoleta.usuario.infrastructure.input.rest.dto;

import java.time.LocalDate;

import com.plazoleta.usuario.domain.model.Rol;

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
public class UsuarioResponse {

    private Long id;
    private String nombre;
    private String apellido;
    private String documentoIdentidad;
    private String celular;
    private LocalDate fechaNacimiento;
    private String correo;
    private Rol rol;
}