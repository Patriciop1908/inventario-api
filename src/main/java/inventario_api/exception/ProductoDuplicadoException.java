package inventario_api.exception;

/**
 * Se lanza cuando se intenta crear un producto con un nombre que ya existe.
 */
public class ProductoDuplicadoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ProductoDuplicadoException(String nombreProducto) {
        super("Ya existe un producto con el nombre '" + nombreProducto + "'");
    }
}
