package com.diego.usuarios.infrastructure.output.security;

import com.diego.usuarios.domain.spi.IPasswordEncoderPort;
import org.springframework.security.crypto.password.PasswordEncoder;

public class BCryptPasswordEncoderAdapter implements IPasswordEncoderPort {

    private final PasswordEncoder passwordEncoder;

    public BCryptPasswordEncoderAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String encode(String password) {
        return passwordEncoder.encode(password);
    }
}