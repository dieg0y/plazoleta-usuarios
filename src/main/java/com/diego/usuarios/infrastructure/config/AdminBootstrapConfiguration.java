package com.diego.usuarios.infrastructure.config;

import com.diego.usuarios.infrastructure.output.jpa.entity.UsuarioEntity;
import com.diego.usuarios.infrastructure.output.jpa.repository.IUsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

@Configuration
public class AdminBootstrapConfiguration {

    @Bean
    public CommandLineRunner bootstrapAdministrator(
            IUsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.bootstrap-admin.enabled:false}") boolean enabled,
            @Value("${app.bootstrap-admin.email:}") String email,
            @Value("${app.bootstrap-admin.password:}") String password,
            @Value("${app.bootstrap-admin.name:}") String name,
            @Value("${app.bootstrap-admin.lastname:}") String lastname,
            @Value("${app.bootstrap-admin.document:}") String document,
            @Value("${app.bootstrap-admin.phone:}") String phone,
            @Value("${app.bootstrap-admin.birthdate:}") String birthdate) {
        return args -> {
            if (!enabled) {
                return;
            }
            if (email.isBlank() || password.isBlank() || name.isBlank() || lastname.isBlank()
                    || document.isBlank() || phone.isBlank() || birthdate.isBlank()) {
                throw new IllegalStateException("All BOOTSTRAP_ADMIN_* values are required when admin bootstrap is enabled");
            }

            Optional<UsuarioEntity> existing = usuarioRepository.findByCorreo(email);
            if (existing.isPresent()) {
                if (!"ROLE_ADMIN".equals(existing.get().getRol())) {
                    throw new IllegalStateException("The configured bootstrap email already belongs to a non-administrator");
                }
                return;
            }

            UsuarioEntity admin = new UsuarioEntity();
            admin.setNombre(name);
            admin.setApellido(lastname);
            admin.setDocumentoIdentidad(document);
            admin.setCelular(phone);
            admin.setFechaNacimiento(LocalDate.parse(birthdate));
            admin.setCorreo(email);
            admin.setClave(passwordEncoder.encode(password));
            admin.setRol("ROLE_ADMIN");
            usuarioRepository.save(admin);
        };
    }
}
