package com.plazoleta.usuario.infrastructure.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * HU5: protección de endpoints por rol.
 *
 *  - POST /login        → público
 *  - POST /clientes     → público (HU8: auto-registro de clientes)
 *  - POST /propietarios → solo ADMINISTRADOR
 *  - POST /empleados    → solo PROPIETARIO
 *  - GET /usuarios/**   → solo ADMINISTRADOR/PROPIETARIO
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            @Value("${app.jwt.secret}") String secreto) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler((request, response, accessDeniedException) ->
                    response.sendError(HttpStatus.FORBIDDEN.value(), "No tienes permisos para esta operación")))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/clientes").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/error").permitAll()
                .requestMatchers(HttpMethod.POST, "/propietarios").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.POST, "/empleados").hasRole("PROPIETARIO")
                .requestMatchers(HttpMethod.GET, "/usuarios/**").hasAnyRole("ADMINISTRADOR", "PROPIETARIO")
                .anyRequest().authenticated())
            .addFilterBefore(new JwtAuthenticationFilter(secreto),
                    UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}