package com.plazoleta.usuario.application.port.input;

import com.plazoleta.usuario.domain.model.Usuario;

/**
 * PUERTO DE ENTRADA HU8: crear la cuenta de un cliente.
 * Auto-registro público con las mismas validaciones de HU1,
 * rol forzado a CLIENTE.
 */
public interface CrearClienteUseCase {

    Usuario crearCliente(Usuario cliente);
}