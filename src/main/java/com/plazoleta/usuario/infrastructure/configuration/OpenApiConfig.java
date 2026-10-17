package com.plazoleta.usuario.infrastructure.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI plazoletaUsuarioOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Plazoleta de Comidas - Microservicio Usuario")
                        .description("API para la gestión de usuarios del challenge de Plazoleta. "
                                + "HU1: creación de propietarios.")
                        .version("v1.0.0"));
    }
}