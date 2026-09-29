package com.diego.usuarios.application.usecase;

import com.diego.usuarios.domain.exception.CelularInvalidoException;
import com.diego.usuarios.domain.exception.CorreoDuplicadoException;
import com.diego.usuarios.domain.exception.CorreoInvalidoException;
import com.diego.usuarios.domain.exception.CredencialesInvalidasException;
import com.diego.usuarios.domain.exception.DocumentoInvalidoException;
import com.diego.usuarios.domain.exception.MenorDeEdadException;
import com.diego.usuarios.domain.exception.RestauranteNoAutorizadoException;
import com.diego.usuarios.domain.model.Usuario;
import com.diego.usuarios.domain.spi.IPasswordEncoderPort;
import com.diego.usuarios.domain.spi.IRestauranteOwnershipPort;
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

    @Mock
    private IRestauranteOwnershipPort restauranteOwnershipPort;

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

    @Test
    @DisplayName("Debe lanzar CorreoDuplicadoException cuando el correo ya existe")
    void lanzarExcepcionCorreoDuplicado() {
        when(usuarioPersistencePort.existePorCorreo(usuarioValido.getCorreo())).thenReturn(true);

        assertThrows(CorreoDuplicadoException.class, () -> usuarioUseCase.guardarPropietario(usuarioValido));
        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }

    @Test
    @DisplayName("Debe guardar un cliente cifrando su clave y asignándole el rol cliente")
    void guardarCliente() {
        when(passwordEncoderPort.encode("password123")).thenReturn("claveEncriptadaBCrypt");

        usuarioUseCase.guardarCliente(usuarioValido);

        assertEquals("ROLE_CLIENTE", usuarioValido.getRol());
        assertEquals("claveEncriptadaBCrypt", usuarioValido.getClave());
        verify(usuarioPersistencePort).guardarUsuario(usuarioValido);
    }

    @Test
    @DisplayName("Debe impedir que un propietario cree empleados para un restaurante ajeno")
    void impedirEmpleadoEnRestauranteAjeno() {
        when(restauranteOwnershipPort.esPropietario(10L, 20L, "Bearer token")).thenReturn(false);

        assertThrows(RestauranteNoAutorizadoException.class,
                () -> usuarioUseCase.guardarEmpleado(usuarioValido, 10L, 20L, "Bearer token"));
        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }

    @Test
    @DisplayName("Debe crear un empleado vinculado al restaurante del propietario autenticado")
    void crearEmpleadoParaSuRestaurante() {
        when(restauranteOwnershipPort.esPropietario(10L, 20L, "Bearer token")).thenReturn(true);
        when(passwordEncoderPort.encode("password123")).thenReturn("claveEncriptadaBCrypt");

        usuarioUseCase.guardarEmpleado(usuarioValido, 10L, 20L, "Bearer token");

        assertEquals("ROLE_EMPLEADO", usuarioValido.getRol());
        assertEquals(10L, usuarioValido.getRestauranteId());
        assertEquals("claveEncriptadaBCrypt", usuarioValido.getClave());
        verify(usuarioPersistencePort).guardarUsuario(usuarioValido);
    }

    @Test
    @DisplayName("Debe autenticar solo con una clave válida")
    void autenticarUsuario() {
        when(usuarioPersistencePort.buscarPorCorreo(usuarioValido.getCorreo())).thenReturn(java.util.Optional.of(usuarioValido));
        when(passwordEncoderPort.matches("password123", "password123")).thenReturn(true);
        usuarioValido.setClave("password123");

        assertEquals(usuarioValido, usuarioUseCase.autenticar(usuarioValido.getCorreo(), "password123"));
    }

    @Test
    @DisplayName("Debe ocultar si falló el correo o la clave en un inicio de sesión inválido")
    void rechazarCredencialesInvalidas() {
        when(usuarioPersistencePort.buscarPorCorreo(usuarioValido.getCorreo())).thenReturn(java.util.Optional.of(usuarioValido));
        when(passwordEncoderPort.matches("wrong-password", usuarioValido.getClave())).thenReturn(false);

        assertThrows(CredencialesInvalidasException.class,
                () -> usuarioUseCase.autenticar(usuarioValido.getCorreo(), "wrong-password"));
    }
}