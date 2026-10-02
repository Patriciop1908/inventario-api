package inventario_api.domain;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Objects;

import inventario_api.exception.StockInsuficienteException;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Producto del inventario.
 * <p>
 * Se mapea sobre la misma tabla {@code productos} que usa el DAO de inventario-java,
 * así ambos proyectos pueden trabajar sobre el mismo archivo {@code inventario.db}.
 * Los nombres no distinguen mayúsculas/minúsculas: la clave primaria es el nombre normalizado.
 */
@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @Column(name = "clave")
    private String clave;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    // Se guarda como TEXT para conservar el valor exacto del BigDecimal
    @Convert(converter = BigDecimalTextoConverter.class)
    @Column(name = "precio", nullable = false)
    private BigDecimal precio;

    @Column(name = "cantidad", nullable = false)
    private int stock;

    /** Constructor requerido por JPA. */
    protected Producto() {
    }

    /**
     * Crea un nuevo producto.
     *
     * @throws IllegalArgumentException si algún dato no es válido
     */
    public Producto(String nombre, BigDecimal precio, int stock) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío");
        }
        Objects.requireNonNull(precio, "El precio no puede ser nulo");
        if (precio.signum() < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo: " + precio);
        }
        validarStock(stock);
        this.nombre = nombre.trim();
        this.clave = normalizar(nombre);
        this.precio = precio;
        this.stock = stock;
    }

    public String getNombre() {
        return nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public int getStock() {
        return stock;
    }

    /**
     * Resta unidades del stock.
     *
     * @throws IllegalArgumentException   si las unidades no son positivas
     * @throws StockInsuficienteException si no hay stock suficiente
     */
    public void quitarStock(int unidades) {
        if (unidades <= 0) {
            throw new IllegalArgumentException("Las unidades a retirar deben ser positivas: " + unidades);
        }
        if (unidades > stock) {
            throw new StockInsuficienteException(nombre, unidades, stock);
        }
        stock -= unidades;
    }

    /**
     * Fija el stock a un valor nuevo.
     *
     * @throws IllegalArgumentException si el stock es negativo
     */
    public void actualizarStock(int nuevoStock) {
        validarStock(nuevoStock);
        stock = nuevoStock;
    }

    /** Convierte un nombre en la clave primaria (sin espacios extremos y en minúsculas). */
    public static String normalizar(String nombre) {
        Objects.requireNonNull(nombre, "El nombre no puede ser nulo");
        return nombre.trim().toLowerCase(Locale.ROOT);
    }

    private static void validarStock(int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo: " + stock);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof Producto otro && clave.equals(otro.clave);
    }

    @Override
    public int hashCode() {
        return clave.hashCode();
    }
}
