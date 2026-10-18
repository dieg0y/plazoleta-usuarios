package com.plazoleta.usuario.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plazoleta.usuario.application.port.output.PasswordEncoderPort;
import com.plazoleta.usuario.application.port.output.UsuarioRepositoryPort;
import com.plazoleta.usuario.domain.exception.BusinessException;
import com.plazoleta.usuario.domain.model.Rol;
import com.plazoleta.usuario.domain.model.Usuario;

@ExtendWith(MockitoExtension.class)
class CrearEmpleadoServiceTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    private CrearEmpleadoService crearEmpleadoService;

    private Usuario empleadoValido;

    @BeforeEach
    void setUp() {
        // ValidadorUsuario REAL: ejercemos las validaciones de verdad.
        // Solo se mockea el puerto que usa para consultar unicidad.
        crearEmpleadoService = new CrearEmpleadoService(
                new ValidadorUsuario(usuarioRepositoryPort),
                usuarioRepositoryPort,
                passwordEncoderPort);

        empleadoValido = Usuario.builder()
                .nombre("Ana")
                .apellido("Gomez")
                .documentoIdentidad("987654321")
                .celular("+573009876543")
                .fechaNacimiento(LocalDate.of(1998, 3, 10))
                .correo("ana.empleado@correo.com")
                .clave("claveSegura123")
                .build();
    }

    @Test
    @DisplayName("Crea un empleado exitosamente: encripta la clave y fuerza el rol a EMPLEADO")
    void debeCrearEmpleadoExitosamente() {
        when(usuarioRepositoryPort.existePorCorreo(anyString())).thenReturn(false);
        when(usuarioRepositoryPort.existePorDocumento(anyString())).thenReturn(false);
        when(passwordEncoderPort.encode("claveSegura123")).thenReturn("claveCodificada");
        when(usuarioRepositoryPort.guardar(any(Usuario.class))).thenAnswer(invocacion -> {
            Usuario recibido = invocacion.getArgument(0);
            recibido.setId(2L);
            return recibido;
        });

        Usuario resultado = crearEmpleadoService.crearEmpleado(empleadoValido);

        assertNotNull(resultado);
        assertEquals(Rol.EMPLEADO, resultado.getRol());
        assertEquals("claveCodificada", resultado.getClave());

        verify(usuarioRepositoryPort).guardar(argThat(u -> u.getRol() == Rol.EMPLEADO
                && "claveCodificada".equals(u.getClave())));
    }

    @Test
    @DisplayName("Rechaza a un menor de edad")
    void debeRechazarMenorDeEdad() {
        empleadoValido.setFechaNacimiento(LocalDate.now().minusYears(17));

        assertThrows(BusinessException.class,
                () -> crearEmpleadoService.crearEmpleado(empleadoValido));

        verify(usuarioRepositoryPort, never()).guardar(any(Usuario.class));
    }

    @Test
    @DisplayName("Rechaza cuando el correo ya existe en la base de datos")
    void debeRechazarCorreoDuplicado() {
        when(usuarioRepositoryPort.existePorCorreo("ana.empleado@correo.com")).thenReturn(true);

        assertThrows(BusinessException.class,
                () -> crearEmpleadoService.crearEmpleado(empleadoValido));

        verify(usuarioRepositoryPort, never()).guardar(any(Usuario.class));
    }

    @Test
    @DisplayName("Rechaza un documento que no es numérico")
    void debeRechazarDocumentoNoNumerico() {
        empleadoValido.setDocumentoIdentidad("abc123");

        assertThrows(BusinessException.class,
                () -> crearEmpleadoService.crearEmpleado(empleadoValido));

        verify(usuarioRepositoryPort, never()).guardar(any(Usuario.class));
    }
}