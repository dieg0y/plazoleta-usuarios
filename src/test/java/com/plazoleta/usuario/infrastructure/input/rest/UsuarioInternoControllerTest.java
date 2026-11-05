package com.plazoleta.usuario.infrastructure.input.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.plazoleta.usuario.application.port.input.ObtenerUsuarioUseCase;
import com.plazoleta.usuario.domain.model.Rol;
import com.plazoleta.usuario.domain.model.Usuario;

@ExtendWith(MockitoExtension.class)
class UsuarioInternoControllerTest {

    private static final String INTERNAL_TOKEN = "test-internal-token-with-at-least-32-characters";

    @Mock
    private ObtenerUsuarioUseCase obtenerUsuarioUseCase;

    @Test
    void retornaSoloElCelularDeUnClienteConCredencialInternaValida() {
        when(obtenerUsuarioUseCase.obtenerUsuarioPorId(18L))
                .thenReturn(Usuario.builder().id(18L).celular("+573001234567").rol(Rol.CLIENTE).build());
        UsuarioInternoController controller = new UsuarioInternoController(obtenerUsuarioUseCase, INTERNAL_TOKEN);

        var response = controller.obtenerCelularCliente(18L, INTERNAL_TOKEN);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("+573001234567", response.getBody().celular());
        verify(obtenerUsuarioUseCase).obtenerUsuarioPorId(18L);
    }

    @Test
    void rechazaCredencialFaltanteOIncorrecta() {
        UsuarioInternoController controller = new UsuarioInternoController(obtenerUsuarioUseCase, INTERNAL_TOKEN);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> controller.obtenerCelularCliente(18L, "incorrecto"));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }

    @Test
    void rechazaExponerCelularDeUsuarioQueNoSeaCliente() {
        when(obtenerUsuarioUseCase.obtenerUsuarioPorId(18L))
                .thenReturn(Usuario.builder().id(18L).celular("+573001234567").rol(Rol.EMPLEADO).build());
        UsuarioInternoController controller = new UsuarioInternoController(obtenerUsuarioUseCase, INTERNAL_TOKEN);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> controller.obtenerCelularCliente(18L, INTERNAL_TOKEN));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }
}
