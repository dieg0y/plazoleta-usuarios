package com.plazoleta.usuario.application.service;

import com.plazoleta.usuario.application.port.input.LoginUseCase;
import com.plazoleta.usuario.application.port.output.PasswordEncoderPort;
import com.plazoleta.usuario.application.port.output.TokenGeneratorPort;
import com.plazoleta.usuario.application.port.output.UsuarioRepositoryPort;
import com.plazoleta.usuario.domain.exception.BusinessException;
import com.plazoleta.usuario.domain.model.Usuario;

/**
 * CASO DE USO HU5: inicio de sesión con correo y clave.
 *
 * Reglas:
 *  1. Correo y clave obligatorios.
 *  2. El usuario debe existir.
 *  3. La clave debe coincidir con el hash BCrypt almacenado.
 *  4. Credenciales inválidas: mensaje genérico (no se revela
 *     si falló el correo o la clave).
 *
 * Sin anotaciones de Spring (regla hexagonal).
 */
public class LoginService implements LoginUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final TokenGeneratorPort tokenGeneratorPort;

    public LoginService(UsuarioRepositoryPort usuarioRepositoryPort,
                        PasswordEncoderPort passwordEncoderPort,
                        TokenGeneratorPort tokenGeneratorPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.tokenGeneratorPort = tokenGeneratorPort;
    }

    @Override
    public String login(String correo, String clave) {
        if (correo == null || correo.isBlank() || clave == null || clave.isBlank()) {
            throw new BusinessException("El correo y la clave son obligatorios");
        }

        Usuario usuario = usuarioRepositoryPort.obtenerPorCorreo(correo)
                .orElseThrow(() -> new BusinessException("Credenciales inválidas"));

        if (!passwordEncoderPort.matches(clave, usuario.getClave())) {
            throw new BusinessException("Credenciales inválidas");
        }

        return tokenGeneratorPort.generar(usuario);
    }
}