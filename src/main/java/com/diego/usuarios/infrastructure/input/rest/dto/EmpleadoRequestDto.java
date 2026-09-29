package com.diego.usuarios.infrastructure.input.rest.dto;

import jakarta.validation.constraints.NotNull;

public class EmpleadoRequestDto extends RegistroUsuarioDto {
    private Long restauranteId;

    @NotNull(message = "El restaurante es obligatorio para crear un empleado")
    public Long getRestauranteId() {
        return restauranteId;
    }

    public void setRestauranteId(Long restauranteId) {
        this.restauranteId = restauranteId;
    }
}
