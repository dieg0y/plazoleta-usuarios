package com.plazoleta.usuario.domain.model;

/**
 * Roles que existen en el negocio de la plazoleta de comidas.
 * El dominio define QUÉ roles hay; no sabe cómo se guardan en BD.
 */
public enum Rol {
    ADMINISTRADOR,
    PROPIETARIO,
    EMPLEADO,
    CLIENTE
}