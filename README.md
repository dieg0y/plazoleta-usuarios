# Microservicio de usuarios — Plazoleta

API para registrar clientes, propietarios y empleados, autenticar usuarios y
emitir JWT compartidos con el microservicio de restaurantes.

## Stack

- Java 17
- Spring Boot 3.3.13
- Spring Web, Spring Data JPA, Bean Validation y Spring Security
- MySQL 8
- BCrypt y JWT (JJWT 0.12.6)
- OpenAPI / Swagger UI

## Requisitos

- JDK 17
- MySQL 8 y la base `plazoleta_usuarios`
- Secreto compartido `APP_JWT_SECRET` con al menos 32 caracteres

## Ejecución en Windows

Desde PowerShell, prepara la base de datos:

```powershell
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS plazoleta_usuarios;"
```

Configura las variables en la misma terminal desde la que iniciarás el
servicio. No guardes credenciales ni secretos en Git:

```powershell
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "<contraseña-local-de-MySQL>"
$env:APP_JWT_SECRET = "<secreto-local-compartido-de-32-caracteres-o-más>"
Set-Location <ruta>\usuario
.\mvnw.cmd spring-boot:run
```

`DB_URL` es opcional; por defecto apunta a
`jdbc:mysql://localhost:3306/plazoleta_usuarios`. Hibernate usa `update` para
desarrollo local. Para probar sin MySQL, `.\mvnw.cmd test` usa H2.

Swagger UI: <http://localhost:8080/swagger-ui/index.html>

## Endpoints principales

| Método | Ruta | Descripción | Acceso |
|---|---|---|---|
| POST | `/login` | Valida correo y clave; devuelve JWT | Público |
| POST | `/clientes` | Auto-registra un cliente | Público |
| POST | `/propietarios` | Crea un propietario | ADMINISTRADOR |
| POST | `/empleados` | Crea un empleado | PROPIETARIO |
| GET | `/usuarios/{id}` | Consulta los datos mínimos de usuario | ADMINISTRADOR o PROPIETARIO |
| GET | `/v3/api-docs` | Contrato OpenAPI | Público |

Los endpoints protegidos esperan el encabezado `Authorization: Bearer {JWT}`.
El usuario se inicializa con cuentas BCrypt únicamente cuando `usuario` está vacía. En una
base nueva, las cuentas de desarrollo son:

| Rol | Correo | Clave local |
|---|---|---|
| ADMINISTRADOR | `admin@plazoleta.com` | `Admin123*` |
| PROPIETARIO | `propietario1@plazoleta.com` | `Propietario123*` |
| PROPIETARIO | `propietario2@plazoleta.com` | `Propietario123*` |
| EMPLEADO | `empleado1@plazoleta.com` | `Empleado123*` |
| EMPLEADO | `empleado2@plazoleta.com` | `Empleado123*` |
| CLIENTE | `cliente1@plazoleta.com` | `Cliente123*` |
| CLIENTE | `cliente2@plazoleta.com` | `Cliente123*` |

La inserción inicial respeta este orden, por lo que en una base recién creada
los IDs de propietarios son 2 (Juan) y 3 (Maria). No borres datos existentes
para forzar esos IDs sin respaldar la base.

## Pruebas y empaquetado

```powershell
.\mvnw.cmd clean verify
```

Para construir y ejecutar la imagen Docker desde la raíz:

```powershell
docker build -t plazoleta-usuario .
docker run --rm -p 8080:8080 `
  -e DB_URL="jdbc:mysql://host.docker.internal:3306/plazoleta_usuarios" `
  -e DB_USERNAME="root" `
  -e DB_PASSWORD="<contraseña>" `
  -e APP_JWT_SECRET="<secreto-compartido>" `
  plazoleta-usuario
```

Este repositorio cubre usuarios y autenticación. Los pedidos no pertenecen a
este microservicio.
