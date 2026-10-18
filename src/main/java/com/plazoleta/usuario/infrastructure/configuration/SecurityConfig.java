package com.plazoleta.usuario.infrastructure.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * HU5: protección de endpoints por rol.
 *
 *  - POST /login        → público
 *  - POST /propietarios → solo ADMINISTRADOR
 *  - POST /empleados    → solo PROPIETARIO (llega con el token
 *                         reenviado desde el microservicio restaurante)
 *  - GET /usuarios/**   → autenticado (lo consume restaurante vía Feign)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
            @Value("${app.jwt.secret}") String secreto) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Público: login, Swagger y errores
                .requestMatchers(HttpMethod.POST, "/login").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/error").permitAll()
                // HU5: protección por rol
                .requestMatchers(HttpMethod.POST, "/propietarios").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.POST, "/empleados").hasRole("PROPIETARIO")
                .requestMatchers(HttpMethod.GET, "/usuarios/**").authenticated()
                .anyRequest().authenticated())
            .addFilterBefore(new JwtAuthenticationFilter(secreto),
                    UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}