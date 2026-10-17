package com.plazoleta.usuario.application.port.output;

import com.plazoleta.usuario.domain.model.Usuario;

/**
 * PUERTO DE SALIDA (driven).
 * Lo que la aplicación NECESITA del exterior para funcionar:
 * guardar usuarios y verificar unicidad (regla de HU1).
 *
 * La aplicación no sabe (ni le importa) si detrás hay MySQL,
 * MongoDB o un archivo de texto: eso lo decide el adaptador.
 */
public interface UsuarioRepositoryPort {

    Usuario guardar(Usuario usuario);

    boolean existePorCorreo(String correo);

    boolean existePorDocumento(String documentoIdentidad);
}