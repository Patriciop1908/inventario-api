package inventario_api.controller.dto;

import java.math.BigDecimal;

import inventario_api.domain.Producto;

/** Representación JSON de un producto. */
public record ProductoResponse(String nombre, BigDecimal precio, int stock) {

    public static ProductoResponse desde(Producto producto) {
        return new ProductoResponse(producto.getNombre(), producto.getPrecio(), producto.getStock());
    }
}
