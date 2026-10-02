package inventario_api.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import inventario_api.domain.Producto;
import inventario_api.exception.ProductoDuplicadoException;
import inventario_api.exception.ProductoNoEncontradoException;
import inventario_api.exception.StockInsuficienteException;
import inventario_api.repository.ProductoRepository;

/**
 * Lógica de negocio del inventario.
 * <p>
 * Los cambios de stock no llaman a save(): dentro de la transacción, JPA detecta
 * que la entidad ha cambiado y la actualiza al confirmar.
 */
@Service
@Transactional(readOnly = true)
public class ProductoService {

    private final ProductoRepository repository;

    public ProductoService(ProductoRepository repository) {
        this.repository = repository;
    }

    /** Devuelve todos los productos en el orden en que se dieron de alta. */
    public List<Producto> listarTodos() {
        return repository.findAllEnOrdenDeAlta();
    }

    /**
     * @throws ProductoNoEncontradoException si el producto no existe
     */
    public Producto consultar(String nombre) {
        return repository.findById(Producto.normalizar(nombre))
                .orElseThrow(() -> new ProductoNoEncontradoException(nombre));
    }

    /**
     * Da de alta un producto nuevo.
     *
     * @throws ProductoDuplicadoException si ya existe un producto con ese nombre
     * @throws IllegalArgumentException   si algún dato no es válido
     */
    @Transactional
    public Producto agregarProducto(String nombre, BigDecimal precio, int stock) {
        Producto producto = new Producto(nombre, precio, stock);
        // Hay que comprobarlo antes: con un id asignado, save() sobrescribiría el producto existente
        if (repository.existsById(Producto.normalizar(nombre))) {
            throw new ProductoDuplicadoException(producto.getNombre());
        }
        return repository.save(producto);
    }

    /**
     * Retira unidades del stock de un producto.
     *
     * @throws ProductoNoEncontradoException si el producto no existe
     * @throws StockInsuficienteException    si no hay unidades suficientes
     */
    @Transactional
    public Producto quitarStock(String nombre, int unidades) {
        Producto producto = consultar(nombre);
        producto.quitarStock(unidades);
        return producto;
    }

    /**
     * Fija el stock de un producto a un valor nuevo.
     *
     * @throws ProductoNoEncontradoException si el producto no existe
     */
    @Transactional
    public Producto actualizarStock(String nombre, int nuevoStock) {
        Producto producto = consultar(nombre);
        producto.actualizarStock(nuevoStock);
        return producto;
    }
}
