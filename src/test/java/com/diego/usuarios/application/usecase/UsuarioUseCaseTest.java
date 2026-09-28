package com.diego.usuarios.application.usecase;

import com.diego.usuarios.domain.exception.CelularInvalidoException;
import com.diego.usuarios.domain.exception.CorreoInvalidoException;
import com.diego.usuarios.domain.exception.DocumentoInvalidoException;
import com.diego.usuarios.domain.exception.MenorDeEdadException;
import com.diego.usuarios.domain.model.Usuario;
import com.diego.usuarios.domain.spi.IPasswordEncoderPort;
import com.diego.usuarios.domain.spi.IUsuarioPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioUseCaseTest {

    @Mock
    private IUsuarioPersistencePort usuarioPersistencePort;

    @Mock
    private IPasswordEncoderPort passwordEncoderPort;

    @InjectMocks
    private UsuarioUseCase usuarioUseCase;

    private Usuario usuarioValido;

    @BeforeEach
    void setUp() {
        usuarioValido = new Usuario();
        usuarioValido.setNombre("Carlos");
        usuarioValido.setApellido("Pérez");
        usuarioValido.setDocumentoIdentidad("123456789");
        usuarioValido.setCelular("+573001234567");
        usuarioValido.setFechaNacimiento(LocalDate.of(1995, 5, 15));
        usuarioValido.setCorreo("carlos.perez@email.com");
        usuarioValido.setClave("password123");
    }

    @Test
    @DisplayName("Debe guardar un propietario exitosamente si cumple con todas las validaciones")
    void guardarPropietarioExitoso() {
        when(usuarioPersistencePort.existePorCorreo(usuarioValido.getCorreo())).thenReturn(false);
        when(passwordEncoderPort.encode("password123")).thenReturn("claveEncriptadaBCrypt");

        assertDoesNotThrow(() -> usuarioUseCase.guardarPropietario(usuarioValido));

        verify(usuarioPersistencePort, times(1)).guardarUsuario(any(Usuario.class));
        assertEquals("claveEncriptadaBCrypt", usuarioValido.getClave());
        assertEquals("ROLE_PROPIETARIO", usuarioValido.getRol());
    }

    @Test
    @DisplayName("Debe lanzar MenorDeEdadException cuando el usuario es menor de 18 años")
    void lanzarExcepcionCuandoEsMenorDeEdad() {
        usuarioValido.setFechaNacimiento(LocalDate.now().minusYears(17));

        assertThrows(MenorDeEdadException.class, () -> usuarioUseCase.guardarPropietario(usuarioValido));
        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }

    @Test
    @DisplayName("Debe lanzar DocumentoInvalidoException cuando el documento no es numérico")
    void lanzarExcepcionDocumentoNoNumerico() {
        usuarioValido.setDocumentoIdentidad("12345ABC");

        assertThrows(DocumentoInvalidoException.class, () -> usuarioUseCase.guardarPropietario(usuarioValido));
        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }

    @Test
    @DisplayName("Debe lanzar CelularInvalidoException cuando el celular supera los 13 caracteres")
    void lanzarExcepcionCelularLargo() {
        usuarioValido.setCelular("+57300123456789");

        assertThrows(CelularInvalidoException.class, () -> usuarioUseCase.guardarPropietario(usuarioValido));
        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }

    @Test
    @DisplayName("Debe lanzar CorreoInvalidoException cuando el formato del correo no es válido")
    void lanzarExcepcionCorreoInvalido() {
        usuarioValido.setCorreo("correo-invalido.com");

        assertThrows(CorreoInvalidoException.class, () -> usuarioUseCase.guardarPropietario(usuarioValido));
        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }
}