package com.plazoleta.usuario.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.plazoleta.usuario.application.port.input.CrearEmpleadoUseCase;
import com.plazoleta.usuario.application.port.input.CrearPropietarioUseCase;
import com.plazoleta.usuario.application.port.input.ObtenerUsuarioUseCase;
import com.plazoleta.usuario.application.port.output.PasswordEncoderPort;
import com.plazoleta.usuario.application.port.output.UsuarioRepositoryPort;
import com.plazoleta.usuario.application.service.CrearEmpleadoService;
import com.plazoleta.usuario.application.service.CrearPropietarioService;
import com.plazoleta.usuario.application.service.ObtenerUsuarioService;
import com.plazoleta.usuario.application.service.ValidadorUsuario;

@Configuration
public class BeanConfiguration {

    @Bean
    public ValidadorUsuario validadorUsuario(UsuarioRepositoryPort usuarioRepositoryPort) {
        return new ValidadorUsuario(usuarioRepositoryPort);
    }

    @Bean
    public CrearPropietarioUseCase crearPropietarioUseCase(ValidadorUsuario validadorUsuario,
            UsuarioRepositoryPort usuarioRepositoryPort,
            PasswordEncoderPort passwordEncoderPort) {
        return new CrearPropietarioService(validadorUsuario, usuarioRepositoryPort, passwordEncoderPort);
    }

    @Bean
    public CrearEmpleadoUseCase crearEmpleadoUseCase(ValidadorUsuario validadorUsuario,
            UsuarioRepositoryPort usuarioRepositoryPort,
            PasswordEncoderPort passwordEncoderPort) {
        return new CrearEmpleadoService(validadorUsuario, usuarioRepositoryPort, passwordEncoderPort);
    }

    @Bean
    public ObtenerUsuarioUseCase obtenerUsuarioUseCase(UsuarioRepositoryPort usuarioRepositoryPort) {
        return new ObtenerUsuarioService(usuarioRepositoryPort);
    }
}