package com.diego.usuarios.application.usecase;

import com.diego.usuarios.domain.api.IUsuarioServicePort;
import com.diego.usuarios.domain.exception.CelularInvalidoException;
import com.diego.usuarios.domain.exception.CorreoDuplicadoException;
import com.diego.usuarios.domain.exception.CorreoInvalidoException;
import com.diego.usuarios.domain.exception.CredencialesInvalidasException;
import com.diego.usuarios.domain.exception.DocumentoInvalidoException;
import com.diego.usuarios.domain.exception.MenorDeEdadException;
import com.diego.usuarios.domain.exception.RestauranteNoAutorizadoException;
import com.diego.usuarios.domain.exception.UsuarioDuplicadoException;
import com.diego.usuarios.domain.model.Usuario;
import com.diego.usuarios.domain.spi.IPasswordEncoderPort;
import com.diego.usuarios.domain.spi.IRestauranteOwnershipPort;
import com.diego.usuarios.domain.spi.IUsuarioPersistencePort;

import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

public class UsuarioUseCase implements IUsuarioServicePort {

    private static final Pattern DOCUMENTO_PATTERN = Pattern.compile("^\\d+$");
    private static final Pattern CORREO_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern CELULAR_PATTERN = Pattern.compile("^(?:[0-9]{1,13}|\\+[0-9]{1,12})$");
    private static final int EDAD_MINIMA = 18;

    private final IUsuarioPersistencePort usuarioPersistencePort;
    private final IPasswordEncoderPort passwordEncoderPort;
    private final IRestauranteOwnershipPort restauranteOwnershipPort;

    public UsuarioUseCase(IUsuarioPersistencePort usuarioPersistencePort,
                          IPasswordEncoderPort passwordEncoderPort,
                          IRestauranteOwnershipPort restauranteOwnershipPort) {
        this.usuarioPersistencePort = usuarioPersistencePort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.restauranteOwnershipPort = restauranteOwnershipPort;
    }

    @Override
    public void guardarPropietario(Usuario usuario) {
        validarDatosComunes(usuario);
        validarEdad(usuario.getFechaNacimiento());
        guardarConRol(usuario, "ROLE_PROPIETARIO");
    }

    @Override
    public void guardarEmpleado(Usuario usuario, Long restauranteId, Long propietarioId, String authorization) {
        validarDatosComunes(usuario);
        if (restauranteId == null || !restauranteOwnershipPort.esPropietario(restauranteId, propietarioId, authorization)) {
            throw new RestauranteNoAutorizadoException();
        }
        usuario.setRestauranteId(restauranteId);
        guardarConRol(usuario, "ROLE_EMPLEADO");
    }

    @Override
    public void guardarCliente(Usuario usuario) {
        validarDatosComunes(usuario);
        guardarConRol(usuario, "ROLE_CLIENTE");
    }

    @Override
    public Usuario autenticar(String correo, String clave) {
        if (correo == null || clave == null) {
            throw new CredencialesInvalidasException();
        }
        Usuario usuario = usuarioPersistencePort.buscarPorCorreo(correo)
                .orElseThrow(CredencialesInvalidasException::new);
        if (!passwordEncoderPort.matches(clave, usuario.getClave())) {
            throw new CredencialesInvalidasException();
        }
        return usuario;
    }

    @Override
    public boolean usuarioTieneRol(Long usuarioId, String rol) {
        if (usuarioId == null || rol == null) {
            return false;
        }
        return usuarioPersistencePort.buscarPorId(usuarioId)
                .map(usuario -> rol.equals(usuario.getRol()))
                .orElse(false);
    }

    private void guardarConRol(Usuario usuario, String rol) {
        if (usuarioPersistencePort.existePorCorreo(usuario.getCorreo())) {
            throw new CorreoDuplicadoException("El correo ya está registrado");
        }
        if (usuarioPersistencePort.existePorDocumento(usuario.getDocumentoIdentidad())) {
            throw new UsuarioDuplicadoException("El documento de identidad ya está registrado");
        }
        usuario.setClave(passwordEncoderPort.encode(usuario.getClave()));
        usuario.setRol(rol);
        usuarioPersistencePort.guardarUsuario(usuario);
    }

    private void validarDatosComunes(Usuario usuario) {
        if (usuario == null || esVacio(usuario.getNombre()) || esVacio(usuario.getApellido())) {
            throw new IllegalArgumentException("Nombre y apellido son obligatorios");
        }
        validarDocumento(usuario.getDocumentoIdentidad());
        validarCelular(usuario.getCelular());
        validarCorreo(usuario.getCorreo());
        if (usuario.getClave() == null || usuario.getClave().length() < 8) {
            throw new IllegalArgumentException("La clave debe tener al menos 8 caracteres");
        }
    }

    private boolean esVacio(String valor) {
        return valor == null || valor.isBlank();
    }

    private void validarEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null || fechaNacimiento.isAfter(LocalDate.now())) {
            throw new MenorDeEdadException("La fecha de nacimiento debe ser válida y es obligatoria");
        }
        if (Period.between(fechaNacimiento, LocalDate.now()).getYears() < EDAD_MINIMA) {
            throw new MenorDeEdadException("El usuario debe ser mayor de edad (mínimo 18 años)");
        }
    }

    private void validarDocumento(String documento) {
        if (documento == null || !DOCUMENTO_PATTERN.matcher(documento).matches()) {
            throw new DocumentoInvalidoException("El documento de identidad debe ser solo numérico");
        }
    }

    private void validarCelular(String celular) {
        if (celular == null || !CELULAR_PATTERN.matcher(celular).matches()) {
            throw new CelularInvalidoException("El celular debe contener solo dígitos, puede iniciar con + y no superar los 13 caracteres");
        }
    }

    private void validarCorreo(String correo) {
        if (correo == null || !CORREO_PATTERN.matcher(correo).matches()) {
            throw new CorreoInvalidoException("El formato del correo no es válido");
        }
    }
}
