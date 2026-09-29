package com.diego.usuarios.infrastructure.config;

import com.diego.usuarios.application.usecase.UsuarioUseCase;
import com.diego.usuarios.domain.api.IUsuarioServicePort;
import com.diego.usuarios.domain.spi.IPasswordEncoderPort;
import com.diego.usuarios.domain.spi.IRestauranteOwnershipPort;
import com.diego.usuarios.domain.spi.IUsuarioPersistencePort;
import com.diego.usuarios.infrastructure.output.jpa.adapter.UsuarioMysqlAdapter;
import com.diego.usuarios.infrastructure.output.jpa.mapper.IUsuarioEntityMapper;
import com.diego.usuarios.infrastructure.output.jpa.repository.IUsuarioRepository;
import com.diego.usuarios.infrastructure.output.security.BCryptPasswordEncoderAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BeanConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public IPasswordEncoderPort passwordEncoderPort(PasswordEncoder passwordEncoder) {
        return new BCryptPasswordEncoderAdapter(passwordEncoder);
    }

    @Bean
    public IUsuarioPersistencePort usuarioPersistencePort(IUsuarioRepository usuarioRepository,
                                                           IUsuarioEntityMapper usuarioEntityMapper) {
        return new UsuarioMysqlAdapter(usuarioRepository, usuarioEntityMapper);
    }

    @Bean
    public IUsuarioServicePort usuarioServicePort(IUsuarioPersistencePort usuarioPersistencePort,
                                                   IPasswordEncoderPort passwordEncoderPort,
                                                   IRestauranteOwnershipPort restauranteOwnershipPort) {
        return new UsuarioUseCase(usuarioPersistencePort, passwordEncoderPort, restauranteOwnershipPort);
    }
}