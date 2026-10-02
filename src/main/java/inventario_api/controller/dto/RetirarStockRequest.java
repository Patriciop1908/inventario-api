package inventario_api.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** Cuerpo de POST /productos/{nombre}/stock/retiros: las unidades a retirar. */
public record RetirarStockRequest(
        @NotNull(message = "Las unidades son obligatorias")
        @Positive(message = "Las unidades a retirar deben ser positivas")
        Integer unidades) {
}
