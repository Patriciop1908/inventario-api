# Inventario API

API REST para gestionar el inventario de una tienda: alta de productos, consultas y movimientos de stock. Está construida con **Java 25 y Spring Boot 4**, y guarda los datos en **SQLite**.

![Java](https://img.shields.io/badge/Java-25-orange) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-brightgreen) ![SQLite](https://img.shields.io/badge/SQLite-3-blue) ![Tests](https://img.shields.io/badge/tests-JUnit%20%2B%20MockMvc-success)

## Funcionalidades

- Dar de alta productos con nombre, precio y stock inicial.
- Consultar todos los productos o uno concreto por su nombre. El nombre no distingue mayúsculas de minúsculas.
- Fijar el stock de un producto a un valor nuevo.
- Retirar unidades del stock, con control de stock insuficiente.
- Validar los datos de entrada y devolver errores claros en formato estándar **Problem Details (RFC 9457)**.

## Tecnologías

| Área | Herramientas |
|---|---|
| Lenguaje | Java 25 |
| Framework | Spring Boot 4 (Spring Web MVC, Spring Data JPA, Bean Validation) |
| Persistencia | Hibernate + SQLite (`sqlite-jdbc`, dialecto de Hibernate Community) |
| Pruebas | JUnit 5, Spring Boot Test, MockMvc |
| Construcción | Maven (incluye Maven Wrapper) |

## Arquitectura

El código está organizado en capas, y cada capa solo se comunica con la siguiente:

```
Controller  →  Service  →  Repository  →  SQLite
(REST, DTOs)   (reglas de    (Spring Data
                negocio)      JPA)
```

```
src/main/java/inventario_api/
├── controller/        Endpoints REST y manejo centralizado de errores (ManejadorErrores)
│   └── dto/           Objetos de entrada y salida, con sus validaciones
├── service/           Lógica de negocio y transacciones
├── repository/        Acceso a datos con Spring Data JPA
├── domain/            Entidad Producto
└── exception/         Excepciones de negocio
```

Algunas decisiones de diseño:
- **Precios con `BigDecimal`**, nunca `double`, para no perder céntimos por redondeo. En la base de datos se guardan como texto para conservar el valor exacto.
- **Los DTOs separan la API de la entidad**: el cliente nunca recibe ni envía directamente la entidad JPA.
- **Una sola conexión a SQLite**, porque SQLite solo admite un escritor a la vez. Así se evitan los errores `SQLITE_BUSY`.

## Cómo ejecutarlo

Requisitos: **JDK 25**. No hace falta instalar Maven, porque el proyecto incluye el Maven Wrapper.

```bash
git clone https://github.com/Patriciop1908/inventario-api.git
cd inventario-api

# Linux / macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

La API arranca en `http://localhost:8080`. La base de datos `inventario.db` se crea automáticamente en la carpeta desde la que se ejecuta.

## Endpoints

| Método | Ruta | Descripción | Respuesta correcta |
|---|---|---|---|
| `GET` | `/productos` | Lista todos los productos, en orden de alta | `200 OK` |
| `GET` | `/productos/{nombre}` | Consulta un producto | `200 OK` |
| `POST` | `/productos` | Crea un producto | `201 Created` + cabecera `Location` |
| `PUT` | `/productos/{nombre}/stock` | Fija el stock a un valor nuevo | `200 OK` |
| `POST` | `/productos/{nombre}/stock/retiros` | Retira unidades del stock | `200 OK` |

Códigos de error: `400` si los datos no son válidos, `404` si el producto no existe y `409` si el producto ya existe o no hay stock suficiente.

### Ejemplos

Crear un producto:

```bash
curl -X POST http://localhost:8080/productos \
  -H "Content-Type: application/json" \
  -d '{"nombre": "Teclado", "precio": 24.99, "stock": 10}'
```

```json
{ "nombre": "Teclado", "precio": 24.99, "stock": 10 }
```

Retirar 3 unidades:

```bash
curl -X POST http://localhost:8080/productos/teclado/stock/retiros \
  -H "Content-Type: application/json" \
  -d '{"unidades": 3}'
```

Si los datos no son válidos, la respuesta indica qué campo ha fallado y por qué (respuesta resumida):

```json
{
  "status": 400,
  "detail": "Los datos enviados no son válidos",
  "errores": { "precio": "El precio no puede ser negativo" }
}
```

## Pruebas

```bash
./mvnw test
```

Las pruebas de integración recorren la API completa con **MockMvc**: creación, consulta, actualización y retirada de stock, más los casos de error (400, 404 y 409). Usan una base de datos SQLite **en memoria**, así que no tocan `inventario.db`.

## Autor

**Gustavo Patricio Pérez Chávez** · Estudiante del Máster en Ingeniería Informática (UCM)
[LinkedIn](https://www.linkedin.com/in/gustavo-patricio-perez-chavez-4a16603a9) · [GitHub](https://github.com/Patriciop1908)
