package com.plazoleta.usuario.infrastructure.configuration;

import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.plazoleta.usuario.application.port.output.PasswordEncoderPort;
import com.plazoleta.usuario.domain.model.Rol;
import com.plazoleta.usuario.infrastructure.output.persistence.entity.UsuarioEntity;
import com.plazoleta.usuario.infrastructure.output.persistence.repository.UsuarioJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UsuarioJpaRepository usuarioRepository;
    private final PasswordEncoderPort passwordEncoder;

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            return;
        }

        usuarioRepository.saveAll(List.of(
                usuario("Administrador", "Plazoleta", "1000000001", "+573000000001",
                        "admin@plazoleta.com", "Admin123*", Rol.ADMINISTRADOR),
                usuario("Juan", "Propietario", "1000000002", "+573000000002",
                        "propietario1@plazoleta.com", "Propietario123*", Rol.PROPIETARIO),
                usuario("Maria", "Propietaria", "1000000003", "+573000000003",
                        "propietario2@plazoleta.com", "Propietario123*", Rol.PROPIETARIO),
                usuario("Ana", "Empleado", "1000000004", "+573000000004",
                        "empleado1@plazoleta.com", "Empleado123*", Rol.EMPLEADO),
                usuario("Carlos", "Empleado", "1000000005", "+573000000005",
                        "empleado2@plazoleta.com", "Empleado123*", Rol.EMPLEADO),
                usuario("Laura", "Cliente", "1000000006", "+573000000006",
                        "cliente1@plazoleta.com", "Cliente123*", Rol.CLIENTE),
                usuario("Pedro", "Cliente", "1000000007", "+573000000007",
                        "cliente2@plazoleta.com", "Cliente123*", Rol.CLIENTE)));
    }

    private UsuarioEntity usuario(String nombre, String apellido, String documento, String celular,
            String correo, String clave, Rol rol) {
        return UsuarioEntity.builder()
                .nombre(nombre)
                .apellido(apellido)
                .documentoIdentidad(documento)
                .celular(celular)
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .correo(correo)
                .clave(passwordEncoder.encode(clave))
                .rol(rol)
                .build();
    }
}
