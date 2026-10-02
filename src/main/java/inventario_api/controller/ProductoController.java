package inventario_api.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import inventario_api.controller.dto.ActualizarStockRequest;
import inventario_api.controller.dto.CrearProductoRequest;
import inventario_api.controller.dto.ProductoResponse;
import inventario_api.controller.dto.RetirarStockRequest;
import inventario_api.domain.Producto;
import inventario_api.service.ProductoService;
import jakarta.validation.Valid;

/**
 * API REST del inventario. Los errores se traducen a respuestas HTTP en {@link ManejadorErrores}.
 */
@RestController
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProductoResponse> listarTodos() {
        return service.listarTodos().stream()
                .map(ProductoResponse::desde)
                .toList();
    }

    @GetMapping("/{nombre}")
    public ProductoResponse consultar(@PathVariable String nombre) {
        return ProductoResponse.desde(service.consultar(nombre));
    }

    /** Crea un producto y responde 201 con la cabecera Location apuntando a él. */
    @PostMapping
    public ResponseEntity<ProductoResponse> crear(@Valid @RequestBody CrearProductoRequest request) {
        Producto producto = service.agregarProducto(request.nombre(), request.precio(), request.stock());
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{nombre}")
                .buildAndExpand(producto.getNombre())
                .toUri();
        return ResponseEntity.created(ubicacion).body(ProductoResponse.desde(producto));
    }

    /** Fija el stock a un valor concreto (idempotente, como corresponde a PUT). */
    @PutMapping("/{nombre}/stock")
    public ProductoResponse actualizarStock(@PathVariable String nombre,
                                            @Valid @RequestBody ActualizarStockRequest request) {
        return ProductoResponse.desde(service.actualizarStock(nombre, request.stock()));
    }

    /** Retira unidades del stock; responde 409 si no hay suficientes. */
    @PostMapping("/{nombre}/stock/retiros")
    public ProductoResponse retirarStock(@PathVariable String nombre,
                                         @Valid @RequestBody RetirarStockRequest request) {
        return ProductoResponse.desde(service.quitarStock(nombre, request.unidades()));
    }
}
