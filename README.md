# Microservicio de usuarios

Servicio de cuentas, autenticación y autorización para el reto **Plaza de Comidas**. Está construido con Java 17, Spring Boot 3.2 y arquitectura hexagonal.

## Alcance de este repositorio

Este microservicio gestiona el registro de propietarios, empleados y clientes; valida credenciales, emite JWT y verifica roles. La creación/listado de restaurantes y la gestión de platos pertenecen al repositorio hermano `plazoleta-restaurantes`. Los pedidos, notificaciones SMS, trazabilidad y métricas aún no forman parte de estos servicios.

## Estado HU01-HU09

| HU | Historia | Estado en este proyecto |
|---|---|---|
| HU01 | Administrador crea propietario | Implementada. Alta protegida para administradores; valida datos, mayoría de edad y duplicados; persiste rol y clave BCrypt. |
| HU02 | Administrador crea restaurante | Implementada en `plazoleta-restaurantes`, que comprueba aquí que el usuario indicado tenga rol propietario. |
| HU03 | Propietario crea plato | Implementada en `plazoleta-restaurantes`; verifica propietario del restaurante. |
| HU04 | Propietario modifica plato | Implementada en `plazoleta-restaurantes`; únicamente permite cambiar precio y descripción. |
| HU05 | Autenticación y permisos por rol | Implementada para login JWT HS256, expiración de 2 horas y autorización de endpoints. El secreto debe compartirse con el servicio de restaurantes. |
| HU06 | Propietario crea empleados de su restaurante | Implementada; requiere JWT de propietario, restaurante y validación de pertenencia contra el servicio de restaurantes. |
| HU07 | Propietario habilita/deshabilita plato | Implementada en `plazoleta-restaurantes`; limita cambios a restaurantes propios. |
| HU08 | Cliente crea cuenta | Implementada; valida campos y guarda la clave con BCrypt. |
| HU09 | Cliente lista restaurantes | Implementada en `plazoleta-restaurantes`, orden alfabético, paginación y respuesta reducida a nombre/logo. |

> HU02-HU04, HU07 y HU09 son registradas aquí para explicar el flujo completo, pero su código vive en el otro microservicio. No marcar una HU como terminada para una entrega conjunta hasta ejecutar las pruebas de ambos repositorios y probar la integración.

## Endpoints

Base local: `http://localhost:8081`

| Método | Ruta | Permiso | Descripción |
|---|---|---|---|
| `POST` | `/auth/login` | Público | Valida correo/clave y retorna un JWT Bearer. |
| `POST` | `/usuarios/cliente` | Público | Crea una cuenta de cliente. |
| `POST` | `/usuarios/propietario` | `ROLE_ADMIN` | Crea una cuenta de propietario mayor de edad. |
| `POST` | `/usuarios/empleado` | `ROLE_PROPIETARIO` | Crea empleado vinculado a un restaurante que pertenece al propietario autenticado. |
| `GET` | `/internal/usuarios/{id}/roles/{rol}` | `ROLE_ADMIN` | Verificación de rol consumida al crear restaurante. Devuelve `true`/`false`. |
| `GET` | `http://localhost:8082/internal/restaurantes/{restaurantId}/propietarios/{proprietorId}` | `ROLE_PROPIETARIO` | Validación interna que consume `POST /usuarios/empleado`; user service reenvía el JWT del propietario al servicio de restaurantes, que responde `true` solo si el `sub` autenticado coincide con `proprietorId` y es dueño del restaurante. |

Todos los endpoints salvo los marcados públicos requieren `Authorization: Bearer <token>`. Errores de validación devuelven `400`; credenciales incorrectas `401`; usuario duplicado `409`; permisos/propiedad insuficientes `403`; servicio de restaurantes no disponible `503`.

La comprobación de rol HU02 también reenvía el bearer JWT del administrador a `GET /internal/usuarios/{id}/roles/ROLE_PROPIETARIO`. Ambos servicios deben usar el mismo `JWT_SECRET` HS256. Para HU06, el servicio de restaurantes expone el endpoint interno de pertenencia; el microservicio de usuarios no mantiene una copia de restaurantes.

### Ejemplo de login

```http
POST /auth/login
Content-Type: application/json

{
  "correo": "admin@plazoleta.local",
  "clave": "una-clave-configurada"
}
```

Respuesta:

```json
{
  "accessToken": "<jwt>",
  "tokenType": "Bearer",
  "expiresAt": "<fecha-hora-UTC>"
}
```

### Ejemplo para registrar un empleado

```http
POST /usuarios/empleado
Authorization: Bearer <jwt-del-propietario>
Content-Type: application/json

{
  "nombre": "Ana",
  "apellido": "Pérez",
  "documentoIdentidad": "123456789",
  "celular": "+573001234567",
  "correo": "ana@ejemplo.com",
  "clave": "clave-segura",
  "restauranteId": 1
}
```

## Requisitos

- JDK 17 o superior.
- Maven Wrapper incluido (`mvnw.cmd` para Windows).
- Para ejecución local se usa H2 en memoria; las cuentas se pierden al apagar el servicio.
- Para producción/integración, configurar MySQL y reemplazar `ddl-auto=update` por migraciones controladas.

## Configuración local y arranque

Desde esta carpeta en PowerShell:

```powershell
.\mvnw.cmd clean verify
.\mvnw.cmd spring-boot:run
```

Swagger UI: `http://localhost:8081/swagger-ui/index.html`  
OpenAPI JSON: `http://localhost:8081/api-docs`  
H2 Console local: `http://localhost:8081/h2-console`

### Primer administrador

El registro de propietarios está protegido: no existe un endpoint público para crear administradores. Para levantar el primer administrador de desarrollo, habilita el bootstrap por variables de entorno. No uses este mecanismo en cada arranque de producción ni subas las credenciales al repositorio.

Variables requeridas cuando `BOOTSTRAP_ADMIN_ENABLED=true`:

```text
BOOTSTRAP_ADMIN_ENABLED=true
BOOTSTRAP_ADMIN_EMAIL=admin@ejemplo.local
BOOTSTRAP_ADMIN_PASSWORD=<clave>
BOOTSTRAP_ADMIN_NAME=Admin
BOOTSTRAP_ADMIN_LASTNAME=Plazoleta
BOOTSTRAP_ADMIN_DOCUMENT=100000000
BOOTSTRAP_ADMIN_PHONE=3001234567
BOOTSTRAP_ADMIN_BIRTHDATE=1980-01-01
```

El bootstrap crea la cuenta solo si ese correo aún no existe; la contraseña queda cifrada con BCrypt. Deshabilítalo después del aprovisionamiento.

### JWT e integración entre servicios

- `JWT_SECRET`: secreto UTF-8 de al menos 32 bytes para HS256. El valor por defecto solo facilita el arranque local y **no es seguro para despliegue**.
- El microservicio de restaurantes debe usar el mismo `JWT_SECRET` y los mismos claims: `sub` contiene el ID numérico del usuario y `role` uno de `ROLE_ADMIN`, `ROLE_PROPIETARIO`, `ROLE_EMPLEADO` o `ROLE_CLIENTE`.
- `RESTAURANTES_URL`: URL base del microservicio de restaurantes; valor local predeterminado `http://localhost:8082`.
- El alta de empleado llama a `/internal/restaurantes/{restauranteId}/propietarios/{propietarioId}` y reenvía el token del propietario. La indisponibilidad del otro servicio se comunica como error; no se concede acceso como fallback.

No se almacenan tokens en servidor; el JWT expira a las dos horas. La recuperación de contraseña está fuera del alcance declarado por el reto.

## Arquitectura

```text
infrastructure/input/rest  -> adaptadores HTTP, DTO, validación, seguridad
application/usecase        -> reglas y casos de uso
domain/api, domain/spi     -> puertos y modelo del dominio
infrastructure/output      -> JPA, BCrypt, comunicación con restaurantes
```

La persistencia local usa H2; el artefacto incluye el driver MySQL para configurar otra base. MapStruct transforma DTO y entidades. OpenAPI publica la documentación HTTP.

## Pruebas

```powershell
.\mvnw.cmd clean verify
```

Las pruebas cubren reglas de registro, cifrado/validación de contraseña, rechazo de restaurante ajeno, restricciones de acceso HTTP y carga del contexto Spring. La verificación local de HU02-HU04/HU07/HU09 debe ejecutarse además en el repositorio `plazoleta-restaurantes`.

## Pendientes antes de una entrega productiva

- [ ] Crear/publicar en GitHub el repositorio separado `plazoleta-restaurantes` y configurar su remoto.
- [ ] Ejecutar pruebas integradas con ambos servicios activos, mismo `JWT_SECRET` y sus respectivas URLs.
- [ ] Sustituir el secreto JWT local por uno seguro gestionado fuera del repositorio.
- [ ] Decidir y aplicar una estrategia de migraciones SQL, perfiles `dev/test/prod` y configuración de MySQL.
- [ ] Añadir pruebas de integración para el flujo propietario → restaurante → empleado y casos de servicio dependiente no disponible.
- [ ] Configurar CI para compilar y probar cada microservicio por separado.
- [ ] Implementar HU10 en adelante: listar platos, pedidos, flujo de estados, SMS, trazabilidad y métricas.
- [ ] Crear una rama independiente por HU según la guía del classroom y mantener OpenAPI/pruebas por historia.
