# Tickets EventPass

Microservicio de Tickets para el contrato local de EventPass. Emite tickets para órdenes con aforo reservado, permite que un comprador consulte sus propios tickets y permite que STAFF valide cada código una sola vez. La comunicación local se realiza por HTTP/JSON; este servicio no implementa componentes AWS.

## Requisitos y configuración

- Java 21
- PostgreSQL
- Microservicio Users disponible para registrar/iniciar sesión y emitir JWT

Crea la base de datos `eventpass_tickets` en PostgreSQL. Copia `.env.example` como `.env` y configura la conexión. `JWT_SECRET_BASE64` debe tener exactamente el mismo valor que el de Users; `JWT_ISSUER` debe ser `eventpass-users`. El archivo `.env` es local y no se versiona.

La aplicación escucha en el puerto `8081` por defecto. Para iniciarla desde CMD en la raíz del repositorio:

```cmd
mvnw.cmd spring-boot:run
```

Variables de entorno disponibles en `.env`:

| Variable | Valor predeterminado / uso |
|---|---|
| `SERVER_PORT` | `8081` |
| `DB_URL` | `jdbc:postgresql://localhost:5432/eventpass_tickets` |
| `DB_USERNAME` | `postgres` |
| `DB_PASSWORD` | Vacío |
| `JWT_SECRET_BASE64` | Obligatoria; debe coincidir con Users |
| `JWT_ISSUER` | `eventpass-users` |

Hibernate actualiza el esquema local con `ddl-auto=update`. Tickets administra su propia tabla y guarda los identificadores externos `ordenId`, `usuarioId` y `eventoId` como valores; no consulta las bases de datos de los otros servicios.

## Autenticación y autorización

Las rutas usan el JWT Bearer emitido por Users. Tickets valida firma HMAC, emisor y expiración con la clave compartida. El token debe incluir `sub`, `email` y `rol`, siguiendo el formato de Users.

| Operación | Rol requerido |
|---|---|
| Emitir tickets para una orden | `COMPRADOR` |
| Consultar mis tickets | `COMPRADOR` |
| Validar un código | `STAFF` |

Para la emisión, Orders debe reenviar el JWT del comprador en `Authorization: Bearer <token>`. Esta modalidad autentica al comprador y evita añadir otra credencial entre servicios. Como Tickets no recibe una identidad propia de Orders, un titular de un JWT `COMPRADOR` válido también puede invocar directamente la ruta interna si conoce los datos de la orden.

## Endpoints del contrato

### Emitir tickets

`POST http://localhost:8081/interno/tickets`

Headers: `Authorization: Bearer <JWT del comprador>` y `Content-Type: application/json`.

Solicitud:

```json
{
  "ordenId": 901,
  "usuarioId": 12,
  "eventoId": 45,
  "cantidad": 2
}
```

Primera emisión: `201 Created`.

```json
{
  "ordenId": 901,
  "tickets": [
    {"ticketId": 1, "codigo": "EVP-7KQ4XH9M2W3A"},
    {"ticketId": 2, "codigo": "EVP-8N5T3Y6P4Z2B"}
  ]
}
```

Los códigos se generan aleatoriamente con prefijo `EVP-`; la base de datos impone su unicidad. Si se reintenta la misma orden con usuario, evento y cantidad iguales, devuelve los tickets existentes con `200 OK`. Si esos datos no coinciden, responde `409 Conflict`. `usuarioId` debe coincidir con el `sub` del JWT.

### Consultar tickets propios

`GET http://localhost:8081/tickets/mis-tickets?usuarioId=12`

Requiere JWT de `COMPRADOR` y que `usuarioId` coincida con el `sub`. Responde `200 OK` con una lista; si no existen tickets, devuelve `[]`.

```json
[
  {
    "ticketId": 1,
    "codigo": "EVP-7KQ4XH9M2W3A",
    "ordenId": 901,
    "eventoId": 45,
    "estado": "EMITIDO"
  }
]
```

### Validar un código

`POST http://localhost:8081/tickets/EVP-7KQ4XH9M2W3A/validaciones`

Requiere JWT de `STAFF`. No necesita cuerpo.

Primera validación: `200 OK`.

```json
{
  "codigo": "EVP-7KQ4XH9M2W3A",
  "estado": "UTILIZADO",
  "valido": true
}
```

Si ya fue utilizado: `409 Conflict`.

```json
{
  "codigo": "EVP-7KQ4XH9M2W3A",
  "estado": "UTILIZADO",
  "valido": false,
  "mensaje": "El código ya fue utilizado."
}
```

Un código inexistente responde `404 Not Found`. Un JWT ausente o inválido responde `401 Unauthorized`; un rol incorrecto responde `403 Forbidden`.

## Prueba manual con Postman

1. Inicia Users y Tickets, y crea o inicia sesión con un comprador para obtener su JWT.
2. Envía `POST /interno/tickets` con el token del comprador y una cantidad mayor que cero.
3. Repite la misma solicitud y confirma que conserva los códigos y devuelve `200`.
4. Cambia la cantidad o el evento para esa misma orden y confirma `409`.
5. Consulta `GET /tickets/mis-tickets?usuarioId={sub}` y comprueba que aparecen esos tickets. Prueba también un `usuarioId` diferente y espera `403`.
6. Inicia sesión con un usuario STAFF y valida un código. Repite la validación y confirma `409`.
7. Comprueba que una petición sin token responde `401` y que el rol equivocado responde `403`.

La emisión presupone que Orders ya obtuvo una reserva confirmada de Events, de acuerdo con el contrato del equipo. Tickets no reserva aforo ni llama directamente a Events.
