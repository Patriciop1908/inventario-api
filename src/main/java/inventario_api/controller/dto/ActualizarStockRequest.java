package inventario_api.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/** Cuerpo de PUT /productos/{nombre}/stock: el nuevo valor del stock. */
public record ActualizarStockRequest(
        @NotNull(message = "El stock es obligatorio")
        @PositiveOrZero(message = "El stock no puede ser negativo")
        Integer stock) {
}
