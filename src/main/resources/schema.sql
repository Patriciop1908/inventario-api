-- Mismo esquema que el InventarioDAO de inventario-java, para poder compartir inventario.db.
-- El precio se guarda como TEXT para conservar exactamente el valor del BigDecimal.
CREATE TABLE IF NOT EXISTS productos (
    clave    TEXT    PRIMARY KEY,
    nombre   TEXT    NOT NULL,
    precio   TEXT    NOT NULL,
    cantidad INTEGER NOT NULL CHECK (cantidad >= 0)
);
