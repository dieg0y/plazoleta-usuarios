package com.plazoleta.usuario;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.plazoleta.usuario.application.port.output.PasswordEncoderPort;
import com.plazoleta.usuario.infrastructure.output.persistence.repository.UsuarioJpaRepository;

@SpringBootTest
class UsuarioApplicationTests {

	@Autowired
	private UsuarioJpaRepository usuarioRepository;

	@Autowired
	private PasswordEncoderPort passwordEncoder;

	@Test
	void contextLoads() {
	}

	@Test
	void seedsDevelopmentAccountsWithBcryptPasswords() {
		assertThat(usuarioRepository.count()).isEqualTo(7);
		var administrador = usuarioRepository.findByCorreo("admin@plazoleta.com").orElseThrow();
		assertThat(administrador.getRol().name()).isEqualTo("ADMINISTRADOR");
		assertThat(passwordEncoder.matches("Admin123*", administrador.getClave())).isTrue();

		assertThat(usuarioRepository.findByCorreo("propietario1@plazoleta.com").orElseThrow().getId())
				.isEqualTo(2L);
		assertThat(usuarioRepository.findByCorreo("propietario2@plazoleta.com").orElseThrow().getId())
				.isEqualTo(3L);
	}
}
