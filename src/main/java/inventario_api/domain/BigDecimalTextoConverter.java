package inventario_api.domain;

import java.math.BigDecimal;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Guarda los BigDecimal como texto en SQLite.
 * <p>
 * Un REAL de SQLite es un double y podría introducir errores de redondeo en los precios;
 * además es el mismo formato que usa el DAO de inventario-java.
 */
@Converter
public class BigDecimalTextoConverter implements AttributeConverter<BigDecimal, String> {

    @Override
    public String convertToDatabaseColumn(BigDecimal valor) {
        return valor == null ? null : valor.toPlainString();
    }

    @Override
    public BigDecimal convertToEntityAttribute(String texto) {
        return texto == null ? null : new BigDecimal(texto);
    }
}
