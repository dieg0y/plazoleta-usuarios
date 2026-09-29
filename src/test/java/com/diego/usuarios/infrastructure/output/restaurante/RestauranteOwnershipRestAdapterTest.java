package com.diego.usuarios.infrastructure.output.restaurante;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class RestauranteOwnershipRestAdapterTest {
    @Test
    void forwardsProprietorJwtToProtectedOwnershipEndpoint() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        RestauranteOwnershipRestAdapter adapter =
                new RestauranteOwnershipRestAdapter(builder, "http://restaurants.test");
        server.expect(requestTo("http://restaurants.test/internal/restaurantes/12/propietarios/34"))
                .andExpect(header("Authorization", "Bearer proprietor-token"))
                .andRespond(withSuccess("true", MediaType.APPLICATION_JSON));

        assertTrue(adapter.esPropietario(12L, 34L, "Bearer proprietor-token"));
        server.verify();
    }

    @Test
    void deniesNonOwnerAndDoesNotCallServiceWithoutAuthorization() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        RestauranteOwnershipRestAdapter adapter =
                new RestauranteOwnershipRestAdapter(builder, "http://restaurants.test");
        server.expect(requestTo("http://restaurants.test/internal/restaurantes/12/propietarios/34"))
                .andRespond(withSuccess("false", MediaType.APPLICATION_JSON));

        assertFalse(adapter.esPropietario(12L, 34L, "Bearer proprietor-token"));
        assertFalse(adapter.esPropietario(12L, 34L, null));
        server.verify();
    }
}
