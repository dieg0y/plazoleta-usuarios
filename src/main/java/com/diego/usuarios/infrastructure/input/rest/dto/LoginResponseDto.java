package com.diego.usuarios.infrastructure.input.rest.dto;

import java.time.Instant;

public record LoginResponseDto(String accessToken, String tokenType, Instant expiresAt) {
}
