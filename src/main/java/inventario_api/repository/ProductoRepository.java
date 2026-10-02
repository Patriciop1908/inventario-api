package inventario_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import inventario_api.domain.Producto;

/**
 * Acceso a los productos. La clave primaria es el nombre normalizado
 * (ver {@link Producto#normalizar(String)}).
 */
public interface ProductoRepository extends JpaRepository<Producto, String> {

    /** Devuelve los productos en el orden en que se dieron de alta (rowid de SQLite). */
    @Query(value = "SELECT * FROM productos ORDER BY rowid", nativeQuery = true)
    List<Producto> findAllEnOrdenDeAlta();
}
