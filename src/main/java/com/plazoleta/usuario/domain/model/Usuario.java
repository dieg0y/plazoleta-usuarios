package com.plazoleta.usuario.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Modelo de dominio: representa a cualquier persona del sistema
 * (administrador, propietario, empleado o cliente) según su rol.
 *
 * Regla hexagonal: es una clase de negocio PURA.
 * Cero anotaciones de Spring, cero JPA, cero JSON.
 * La entidad de base de datos (UsuarioEntity) vivirá en infraestructura
 * y un mapper la convertirá a este modelo.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    private Long id;
    private String nombre;
    private String apellido;
    private String documentoIdentidad;
    private String celular;
    private LocalDate fechaNacimiento;
    private String correo;
    private String clave;
    private Rol rol;
}