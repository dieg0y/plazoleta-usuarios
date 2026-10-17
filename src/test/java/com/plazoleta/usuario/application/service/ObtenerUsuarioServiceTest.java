package com.plazoleta.usuario.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plazoleta.usuario.application.port.output.UsuarioRepositoryPort;
import com.plazoleta.usuario.domain.exception.BusinessException;
import com.plazoleta.usuario.domain.model.Rol;
import com.plazoleta.usuario.domain.model.Usuario;

@ExtendWith(MockitoExtension.class)
class ObtenerUsuarioServiceTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @InjectMocks
    private ObtenerUsuarioService obtenerUsuarioService;

    @Test
    @DisplayName("Obtiene un usuario existente por su id")
    void debeObtenerUsuarioPorId() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .nombre("Diego")
                .correo("diego.test@correo.com")
                .rol(Rol.PROPIETARIO)
                .build();

        when(usuarioRepositoryPort.obtenerPorId(1L)).thenReturn(Optional.of(usuario));

        Usuario resultado = obtenerUsuarioService.obtenerUsuarioPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(Rol.PROPIETARIO, resultado.getRol());
    }

    @Test
    @DisplayName("Lanza excepción cuando el usuario no existe")
    void debeLanzarExcepcionCuandoNoExiste() {
        when(usuarioRepositoryPort.obtenerPorId(99L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> obtenerUsuarioService.obtenerUsuarioPorId(99L));
    }
}