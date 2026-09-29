CREATE TABLE usuarios (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(255) NOT NULL,
    apellido VARCHAR(255) NOT NULL,
    documento_identidad VARCHAR(255) NOT NULL,
    celular VARCHAR(13) NOT NULL,
    fecha_nacimiento DATE NULL,
    correo VARCHAR(255) NOT NULL,
    clave VARCHAR(255) NOT NULL,
    rol VARCHAR(255) NOT NULL,
    restaurante_id BIGINT NULL,
    CONSTRAINT pk_usuarios PRIMARY KEY (id),
    CONSTRAINT uk_usuarios_documento_identidad UNIQUE (documento_identidad),
    CONSTRAINT uk_usuarios_correo UNIQUE (correo)
);
