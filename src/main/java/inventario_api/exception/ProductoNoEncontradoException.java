package inventario_api.exception;

/**
 * Se lanza cuando se busca un producto que no existe.
 */
public class ProductoNoEncontradoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ProductoNoEncontradoException(String nombreProducto) {
        super("No existe ningún producto con el nombre '" + nombreProducto + "'");
    }
}
