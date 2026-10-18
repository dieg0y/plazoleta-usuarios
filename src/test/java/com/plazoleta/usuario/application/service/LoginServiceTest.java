package com.plazoleta.usuario.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plazoleta.usuario.application.port.output.PasswordEncoderPort;
import com.plazoleta.usuario.application.port.output.TokenGeneratorPort;
import com.plazoleta.usuario.application.port.output.UsuarioRepositoryPort;
import com.plazoleta.usuario.domain.exception.BusinessException;
import com.plazoleta.usuario.domain.model.Rol;
import com.plazoleta.usuario.domain.model.Usuario;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    @Mock
    private TokenGeneratorPort tokenGeneratorPort;

    @InjectMocks
    private LoginService loginService;

    private Usuario admin;

    @BeforeEach
    void setUp() {
        admin = Usuario.builder()
                .id(1L)
                .nombre("Admin")
                .apellido("General")
                .documentoIdentidad("999999999")
                .celular("+573000000000")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .correo("admin@plazoleta.com")
                .clave("$2a$10$hashAlmacenado")
                .rol(Rol.ADMINISTRADOR)
                .build();
    }

    @Test
    @DisplayName("Retorna un token cuando el correo y la clave son correctos")
    void debeRetornarTokenConCredencialesCorrectas() {
        when(usuarioRepositoryPort.obtenerPorCorreo("admin@plazoleta.com")).thenReturn(Optional.of(admin));
        when(passwordEncoderPort.matches("admin123", "$2a$10$hashAlmacenado")).thenReturn(true);
        when(tokenGeneratorPort.generar(admin)).thenReturn("jwt-de-prueba");

        String token = loginService.login("admin@plazoleta.com", "admin123");

        assertEquals("jwt-de-prueba", token);
    }

    @Test
    @DisplayName("Rechaza cuando el correo no existe")
    void debeRechazarCorreoInexistente() {
        when(usuarioRepositoryPort.obtenerPorCorreo("desconocido@correo.com")).thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> loginService.login("desconocido@correo.com", "admin123"));

        verify(tokenGeneratorPort, never()).generar(any(Usuario.class));
    }

    @Test
    @DisplayName("Rechaza cuando la clave es incorrecta")
    void debeRechazarClaveIncorrecta() {
        when(usuarioRepositoryPort.obtenerPorCorreo("admin@plazoleta.com")).thenReturn(Optional.of(admin));
        when(passwordEncoderPort.matches("claveMala", "$2a$10$hashAlmacenado")).thenReturn(false);

        assertThrows(BusinessException.class,
                () -> loginService.login("admin@plazoleta.com", "claveMala"));

        verify(tokenGeneratorPort, never()).generar(any(Usuario.class));
    }

    @Test
    @DisplayName("Rechaza cuando faltan correo o clave")
    void debeRechazarCamposVacios() {
        assertThrows(BusinessException.class, () -> loginService.login(null, "admin123"));
        assertThrows(BusinessException.class, () -> loginService.login("admin@plazoleta.com", "  "));

        verify(usuarioRepositoryPort, never()).obtenerPorCorreo(any());
    }
}