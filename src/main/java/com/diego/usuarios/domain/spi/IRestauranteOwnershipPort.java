package com.diego.usuarios.domain.spi;

public interface IRestauranteOwnershipPort {
    boolean esPropietario(Long restauranteId, Long propietarioId, String authorization);
}
