package com.plazoleta.usuario.infrastructure.output.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import com.plazoleta.usuario.application.port.output.TokenGeneratorPort;
import com.plazoleta.usuario.domain.model.Usuario;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * ADAPTADOR DE SALIDA: genera el JWT con JJWT (HS256).
 * El token lleva: subject = id del usuario, claims "role" y "correo".
 * El secreto se comparte con el microservicio restaurante para que
 * pueda validar la firma sin consultarnos.
 */
public class JwtTokenAdapter implements TokenGeneratorPort {

    private final SecretKey key;
    private final long expiracionMs;

    public JwtTokenAdapter(String secreto, long expiracionMs) {
        this.key = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        this.expiracionMs = expiracionMs;
    }

    @Override
    public String generar(Usuario usuario) {
        Instant ahora = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(usuario.getId()))
                .claim("role", usuario.getRol().name())
                .claim("correo", usuario.getCorreo())
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(ahora.plusMillis(expiracionMs)))
                .signWith(key)
                .compact();
    }
}