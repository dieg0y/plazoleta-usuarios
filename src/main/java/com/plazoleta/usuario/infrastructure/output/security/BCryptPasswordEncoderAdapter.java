package com.plazoleta.usuario.infrastructure.output.security;

import com.plazoleta.usuario.application.port.output.PasswordEncoderPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * ADAPTADOR DE SALIDA: implementa el puerto PasswordEncoderPort
 * con el BCryptPasswordEncoder de Spring Security (exigencia de HU1).
 * El algoritmo es un detalle técnico: por eso vive en infraestructura.
 */
@Component
public class BCryptPasswordEncoderAdapter implements PasswordEncoderPort {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public String encode(String clave) {
        return passwordEncoder.encode(clave);
    }

        // HU5: verifica la clave del login contra el hash almacenado
    @Override
    public boolean matches(String clave, String claveEncriptada) {
        return passwordEncoder.matches(clave, claveEncriptada);
    }
}