package com.diego.usuarios.domain.spi;

public interface IPasswordEncoderPort {
    String encode(String password);
}