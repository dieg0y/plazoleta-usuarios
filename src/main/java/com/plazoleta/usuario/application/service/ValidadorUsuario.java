package com.plazoleta.usuario.application.service;

import com.plazoleta.usuario.application.port.output.UsuarioRepositoryPort;
import com.plazoleta.usuario.domain.exception.BusinessException;
import com.plazoleta.usuario.domain.model.Usuario;

import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

/**
 * Validaciones comunes a TODA cuenta de usuario nueva (HU1):
 *  1. Campos obligatorios presentes.
 *  2. Formatos válidos (correo, celular, documento).
 *  3. Mayor de edad y fecha no futura.
 *  4. Unicidad de correo y documento.
 *
 * Lo usan CrearPropietarioService y CrearEmpleadoService (HU4)
 * para no duplicar reglas de negocio (DRY).
 */
public class ValidadorUsuario {

    // ===== Reglas de formato (HU1) =====
    private static final Pattern PATRON_CORREO =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PATRON_CELULAR =
            Pattern.compile("^\\+?\\d+$");
    private static final Pattern PATRON_DOCUMENTO =
            Pattern.compile("^\\d+$");

    private static final int EDAD_MINIMA = 18;
    private static final int LONGITUD_MAXIMA_CELULAR = 13;

    private final UsuarioRepositoryPort usuarioRepositoryPort;

    public ValidadorUsuario(UsuarioRepositoryPort usuarioRepositoryPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
    }

    /** Ejecuta TODAS las validaciones de una cuenta nueva. */
    public void validarNuevoUsuario(Usuario usuario) {
        validarCamposObligatorios(usuario);
        validarFormatos(usuario);
        validarMayorDeEdad(usuario.getFechaNacimiento());
        validarUnicidad(usuario);
    }

    private void validarCamposObligatorios(Usuario usuario) {
        if (usuario == null) {
            throw new BusinessException("El cuerpo de la petición es obligatorio");
        }
        if (esVacio(usuario.getNombre())) {
            throw new BusinessException("El nombre es obligatorio");
        }
        if (esVacio(usuario.getApellido())) {
            throw new BusinessException("El apellido es obligatorio");
        }
        if (esVacio(usuario.getDocumentoIdentidad())) {
            throw new BusinessException("El documento de identidad es obligatorio");
        }
        if (esVacio(usuario.getCelular())) {
            throw new BusinessException("El celular es obligatorio");
        }
        if (usuario.getFechaNacimiento() == null) {
            throw new BusinessException("La fecha de nacimiento es obligatoria");
        }
        if (esVacio(usuario.getCorreo())) {
            throw new BusinessException("El correo es obligatorio");
        }
        if (esVacio(usuario.getClave())) {
            throw new BusinessException("La clave es obligatoria");
        }
    }

    private void validarFormatos(Usuario usuario) {
        if (!PATRON_CORREO.matcher(usuario.getCorreo()).matches()) {
            throw new BusinessException("El correo no tiene una estructura válida");
        }
        if (!PATRON_CELULAR.matcher(usuario.getCelular()).matches()
                || usuario.getCelular().length() > LONGITUD_MAXIMA_CELULAR) {
            throw new BusinessException(
                    "El celular debe contener solo números, un + opcional y máximo 13 caracteres (ej: +573005698325)");
        }
        if (!PATRON_DOCUMENTO.matcher(usuario.getDocumentoIdentidad()).matches()) {
            throw new BusinessException("El documento de identidad debe ser únicamente numérico");
        }
    }

    private void validarMayorDeEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento.isAfter(LocalDate.now())) {
            throw new BusinessException("La fecha de nacimiento no puede ser futura");
        }
        int edad = Period.between(fechaNacimiento, LocalDate.now()).getYears();
        if (edad < EDAD_MINIMA) {
            throw new BusinessException("El usuario debe ser mayor de edad");
        }
    }

    private void validarUnicidad(Usuario usuario) {
        if (usuarioRepositoryPort.existePorCorreo(usuario.getCorreo())) {
            throw new BusinessException(
                    "Ya existe un usuario registrado con el correo " + usuario.getCorreo());
        }
        if (usuarioRepositoryPort.existePorDocumento(usuario.getDocumentoIdentidad())) {
            throw new BusinessException(
                    "Ya existe un usuario registrado con el documento " + usuario.getDocumentoIdentidad());
        }
    }

    private boolean esVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}