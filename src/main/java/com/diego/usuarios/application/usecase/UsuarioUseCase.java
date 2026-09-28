package com.diego.usuarios.application.usecase;

import com.diego.usuarios.domain.api.IUsuarioServicePort;
import com.diego.usuarios.domain.exception.CelularInvalidoException;
import com.diego.usuarios.domain.exception.CorreoInvalidoException;
import com.diego.usuarios.domain.exception.DocumentoInvalidoException;
import com.diego.usuarios.domain.exception.MenorDeEdadException;
import com.diego.usuarios.domain.model.Usuario;
import com.diego.usuarios.domain.spi.IPasswordEncoderPort;
import com.diego.usuarios.domain.spi.IUsuarioPersistencePort;

import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

public class UsuarioUseCase implements IUsuarioServicePort {

    private final IUsuarioPersistencePort usuarioPersistencePort;
    private final IPasswordEncoderPort passwordEncoderPort;

    private static final Pattern DOCUMENTO_PATTERN = Pattern.compile("^\\d+$");
    private static final Pattern CORREO_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final int EDAD_MINIMA = 18;
    private static final int CELULAR_MAX_LENGTH = 13;

    public UsuarioUseCase(IUsuarioPersistencePort usuarioPersistencePort, IPasswordEncoderPort passwordEncoderPort) {
        this.usuarioPersistencePort = usuarioPersistencePort;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    @Override
    public void guardarPropietario(Usuario usuario) {
        validarEdad(usuario.getFechaNacimiento());
        validarDocumento(usuario.getDocumentoIdentidad());
        validarCelular(usuario.getCelular());
        validarCorreo(usuario.getCorreo());

        if (usuarioPersistencePort.existePorCorreo(usuario.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado");
        }

        String claveEncriptada = passwordEncoderPort.encode(usuario.getClave());
        usuario.setClave(claveEncriptada);
        usuario.setRol("ROLE_PROPIETARIO");
        usuarioPersistencePort.guardarUsuario(usuario);
    }

    private void validarEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) {
            throw new MenorDeEdadException("La fecha de nacimiento es obligatoria");
        }
        int edad = Period.between(fechaNacimiento, LocalDate.now()).getYears();
        if (edad < EDAD_MINIMA) {
            throw new MenorDeEdadException("El usuario debe ser mayor de edad (mínimo 18 años)");
        }
    }

    private void validarDocumento(String documento) {
        if (documento == null || !DOCUMENTO_PATTERN.matcher(documento).matches()) {
            throw new DocumentoInvalidoException("El documento de identidad debe ser solo numérico");
        }
    }

    private void validarCelular(String celular) {
        if (celular == null || celular.length() > CELULAR_MAX_LENGTH) {
            throw new CelularInvalidoException("El celular no debe superar los 13 caracteres");
        }
    }

    private void validarCorreo(String correo) {
        if (correo == null || !CORREO_PATTERN.matcher(correo).matches()) {
            throw new CorreoInvalidoException("El formato del correo no es válido");
        }
    }
}