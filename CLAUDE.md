# CLAUDE.md

Convenciones para trabajar en este proyecto (API REST de inventario con Spring Boot y SQLite).

## Idioma

- Los comentarios en el código y los mensajes de error van siempre en español.

## Arquitectura

- Seguir el patrón de capas existente: **Controller → Service → Repository**.
  - `controller/`: endpoints REST, DTOs en `controller/dto/` y manejo de errores en `ManejadorErrores`.
  - `service/`: lógica de negocio.
  - `repository/`: acceso a datos.
  - `domain/`: entidades. `exception/`: excepciones de negocio.
- No saltarse capas (por ejemplo, un Controller no debe usar un Repository directamente).

## Precios y montos

- Usar siempre `BigDecimal` para precios; nunca `double` ni `float`.

## Pruebas

- Toda nueva funcionalidad debe incluir pruebas unitarias en `src/test/java`.
- Antes de dar una tarea por terminada, ejecutar `mvn test` (o `./mvnw test`) y confirmar que todas las pruebas pasen.

## Commits

- Los mensajes de commit deben ser descriptivos y estar en español.
