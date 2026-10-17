package com.plazoleta.usuario.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.plazoleta.usuario.application.port.input.CrearPropietarioUseCase;
import com.plazoleta.usuario.application.port.output.PasswordEncoderPort;
import com.plazoleta.usuario.application.port.output.UsuarioRepositoryPort;
import com.plazoleta.usuario.application.service.CrearPropietarioService;

@Configuration
public class BeanConfiguration {

    @Bean
    public CrearPropietarioUseCase crearPropietarioUseCase(UsuarioRepositoryPort usuarioRepositoryPort,
                                                           PasswordEncoderPort passwordEncoderPort) {
        return new CrearPropietarioService(usuarioRepositoryPort, passwordEncoderPort);
    }
}