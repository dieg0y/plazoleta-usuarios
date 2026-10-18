package com.plazoleta.usuario.application.port.input;

import com.plazoleta.usuario.domain.model.Usuario;

/**
 * PUERTO DE ENTRADA HU4: crear la cuenta de un empleado.
 * Lo consume el microservicio restaurante (vía REST) cuando
 * el propietario registra un empleado para su restaurante.
 */
public interface CrearEmpleadoUseCase {

    Usuario crearEmpleado(Usuario usuario);
}