package com.diego.usuarios.infrastructure.output.restaurante;

import com.diego.usuarios.domain.spi.IRestauranteOwnershipPort;
import com.diego.usuarios.domain.exception.DependenciaNoDisponibleException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Component
public class RestauranteOwnershipRestAdapter implements IRestauranteOwnershipPort {

    private final RestClient restClient;

    public RestauranteOwnershipRestAdapter(RestClient.Builder restClientBuilder,
                                            @Value("${app.restaurantes.url}") String restaurantesUrl) {
        this.restClient = restClientBuilder.baseUrl(restaurantesUrl).build();
    }

    @Override
    public boolean esPropietario(Long restauranteId, Long propietarioId, String authorization) {
        if (authorization == null || authorization.isBlank()) {
            return false;
        }
        try {
            Boolean propietario = restClient.get()
                    .uri("/internal/restaurantes/{restauranteId}/propietarios/{propietarioId}",
                            restauranteId, propietarioId)
                    .header("Authorization", authorization)
                    .retrieve()
                    .body(Boolean.class);
            return Boolean.TRUE.equals(propietario);
        } catch (HttpClientErrorException.NotFound ex) {
            return false;
        } catch (HttpServerErrorException | ResourceAccessException ex) {
            throw new DependenciaNoDisponibleException("plazoleta-restaurantes", ex);
        }
    }
}
