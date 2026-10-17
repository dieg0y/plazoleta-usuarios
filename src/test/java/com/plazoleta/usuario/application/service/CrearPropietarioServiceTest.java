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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plazoleta.usuario.application.port.output.PasswordEncoderPort;
import com.plazoleta.usuario.application.port.output.UsuarioRepositoryPort;
import com.plazoleta.usuario.domain.exception.BusinessException;
import com.plazoleta.usuario.domain.model.Rol;
import com.plazoleta.usuario.domain.model.Usuario;

@ExtendWith(MockitoExtension.class)
class CrearPropietarioServiceTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    @InjectMocks
    private CrearPropietarioService crearPropietarioService;

    private Usuario usuarioValido;

    @BeforeEach
    void setUp() {
        usuarioValido = Usuario.builder()
                .nombre("Diego")
                .apellido("Ramirez")
                .documentoIdentidad("123456789")
                .celular("+573001234567")
                .fechaNacimiento(LocalDate.of(1995, 5, 15))
                .correo("diego.test@correo.com")
                .clave("claveSegura123")
                .build();
    }

    @Test
    @DisplayName("Crea un propietario exitosamente: encripta la clave y fuerza el rol a PROPIETARIO")
    void debeCrearPropietarioExitosamente() {
        // given — el mundo falso: no existe el correo, no existe el documento,
        // el encriptador devuelve una clave fija, y la BD "asigna" el id 1 al guardar
        when(usuarioRepositoryPort.existePorCorreo(anyString())).thenReturn(false);
        when(usuarioRepositoryPort.existePorDocumento(anyString())).thenReturn(false);
        when(passwordEncoderPort.encode("claveSegura123")).thenReturn("claveCodificada");
        when(usuarioRepositoryPort.guardar(any(Usuario.class))).thenAnswer(invocacion -> {
            Usuario recibido = invocacion.getArgument(0);
            recibido.setId(1L); // simula el autoincrement de MySQL
            return recibido;
        });

        // when
        Usuario resultado = crearPropietarioService.crearPropietario(usuarioValido);

        // then — el resultado
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(Rol.PROPIETARIO, resultado.getRol());

        // then — la especia: verificar CON QUÉ datos se llamó a guardar
        verify(usuarioRepositoryPort).guardar(argThat(u ->
                u.getRol() == Rol.PROPIETARIO
                && "claveCodificada".equals(u.getClave())));
    }

    @Test
    @DisplayName("Rechaza un correo con formato inválido, sin tocar la base de datos")
    void debeRechazarCorreoInvalido() {
        usuarioValido.setCorreo("correo-invalido-sin-arroba");

        assertThrows(BusinessException.class,
                () -> crearPropietarioService.crearPropietario(usuarioValido));

        verify(usuarioRepositoryPort, never()).guardar(any(Usuario.class));
        verify(passwordEncoderPort, never()).encode(anyString());
    }

    @Test
    @DisplayName("Rechaza a un menor de edad")
    void debeRechazarMenorDeEdad() {
        usuarioValido.setFechaNacimiento(LocalDate.now().minusYears(17));

        assertThrows(BusinessException.class,
                () -> crearPropietarioService.crearPropietario(usuarioValido));

        verify(usuarioRepositoryPort, never()).guardar(any(Usuario.class));
    }

    @Test
    @DisplayName("Rechaza cuando el correo ya existe en la base de datos")
    void debeRechazarCorreoDuplicado() {
        when(usuarioRepositoryPort.existePorCorreo("diego.test@correo.com")).thenReturn(true);

        assertThrows(BusinessException.class,
                () -> crearPropietarioService.crearPropietario(usuarioValido));

        verify(usuarioRepositoryPort, never()).guardar(any(Usuario.class));
    }
}