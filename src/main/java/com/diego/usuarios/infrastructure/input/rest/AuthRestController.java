package com.diego.usuarios.infrastructure.input.rest;

import com.diego.usuarios.domain.api.IUsuarioServicePort;
import com.diego.usuarios.domain.model.Usuario;
import com.diego.usuarios.infrastructure.input.rest.dto.LoginRequestDto;
import com.diego.usuarios.infrastructure.input.rest.dto.LoginResponseDto;
import jakarta.validation.Valid;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;

@RestController
@RequestMapping("/auth")
public class AuthRestController {

    private static final Duration TOKEN_LIFETIME = Duration.ofHours(2);

    private final IUsuarioServicePort usuarioServicePort;
    private final JwtEncoder jwtEncoder;

    public AuthRestController(IUsuarioServicePort usuarioServicePort, JwtEncoder jwtEncoder) {
        this.usuarioServicePort = usuarioServicePort;
        this.jwtEncoder = jwtEncoder;
    }

    @PostMapping("/login")
    public LoginResponseDto login(@Valid @RequestBody LoginRequestDto request) {
        Usuario usuario = usuarioServicePort.autenticar(request.getCorreo(), request.getClave());
        Instant now = Instant.now();
        Instant expiresAt = now.plus(TOKEN_LIFETIME);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("plazoleta-usuarios")
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(usuario.getId().toString())
                .claim("email", usuario.getCorreo())
                .claim("role", usuario.getRol())
                .build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
        return new LoginResponseDto(token, "Bearer", expiresAt);
    }
}
