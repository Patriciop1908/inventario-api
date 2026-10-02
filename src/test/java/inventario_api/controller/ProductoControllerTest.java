package inventario_api.controller;

import static org.hamcrest.Matchers.endsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import inventario_api.repository.ProductoRepository;

/**
 * Prueba la API de extremo a extremo (controlador, servicio y SQLite en memoria).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductoRepository repository;

    @BeforeEach
    void limpiarBaseDeDatos() {
        repository.deleteAll();
    }

    @Test
    void creaUnProductoYLoConsulta() throws Exception {
        crear("""
                {"nombre": "Teclado", "precio": 19.99, "stock": 10}""")
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/productos/Teclado")))
                .andExpect(jsonPath("$.nombre").value("Teclado"))
                .andExpect(jsonPath("$.precio").value(19.99))
                .andExpect(jsonPath("$.stock").value(10));

        // El nombre no distingue mayúsculas/minúsculas
        mockMvc.perform(get("/productos/teclado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Teclado"))
                .andExpect(jsonPath("$.stock").value(10));
    }

    @Test
    void listaLosProductosEnOrdenDeAlta() throws Exception {
        crear("""
                {"nombre": "Teclado", "precio": 19.99, "stock": 10}""");
        crear("""
                {"nombre": "Monitor", "precio": 150, "stock": 3}""");

        mockMvc.perform(get("/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Teclado"))
                .andExpect(jsonPath("$[1].nombre").value("Monitor"));
    }

    @Test
    void consultarUnProductoInexistenteDevuelve404() throws Exception {
        mockMvc.perform(get("/productos/Raton"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("No existe ningún producto con el nombre 'Raton'"));
    }

    @Test
    void crearConDatosInvalidosDevuelve400ConLosCamposErroneos() throws Exception {
        crear("""
                {"nombre": " ", "precio": -1, "stock": -5}""")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.nombre").value("El nombre es obligatorio"))
                .andExpect(jsonPath("$.errores.precio").value("El precio no puede ser negativo"))
                .andExpect(jsonPath("$.errores.stock").value("El stock no puede ser negativo"));
    }

    @Test
    void crearConJsonMalFormadoDevuelve400() throws Exception {
        crear("{\"nombre\": ")
                .andExpect(status().isBadRequest());
    }

    @Test
    void crearUnProductoDuplicadoDevuelve409() throws Exception {
        crear("""
                {"nombre": "Teclado", "precio": 19.99, "stock": 10}""");

        crear("""
                {"nombre": "TECLADO", "precio": 5, "stock": 1}""")
                .andExpect(status().isConflict());

        // El producto original no se ha sobrescrito
        mockMvc.perform(get("/productos/Teclado"))
                .andExpect(jsonPath("$.stock").value(10));
    }

    @Test
    void actualizaElStock() throws Exception {
        crear("""
                {"nombre": "Teclado", "precio": 19.99, "stock": 10}""");

        actualizarStock("Teclado", """
                {"stock": 25}""")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(25));

        mockMvc.perform(get("/productos/Teclado"))
                .andExpect(jsonPath("$.stock").value(25));
    }

    @Test
    void actualizarStockNegativoDevuelve400() throws Exception {
        crear("""
                {"nombre": "Teclado", "precio": 19.99, "stock": 10}""");

        actualizarStock("Teclado", """
                {"stock": -1}""")
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizarStockDeUnProductoInexistenteDevuelve404() throws Exception {
        actualizarStock("Raton", """
                {"stock": 5}""")
                .andExpect(status().isNotFound());
    }

    @Test
    void retiraStock() throws Exception {
        crear("""
                {"nombre": "Teclado", "precio": 19.99, "stock": 10}""");

        retirarStock("Teclado", """
                {"unidades": 4}""")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(6));
    }

    @Test
    void retirarMasStockDelDisponibleDevuelve409() throws Exception {
        crear("""
                {"nombre": "Teclado", "precio": 19.99, "stock": 3}""");

        retirarStock("Teclado", """
                {"unidades": 5}""")
                .andExpect(status().isConflict());

        mockMvc.perform(get("/productos/Teclado"))
                .andExpect(jsonPath("$.stock").value(3));
    }

    private ResultActions crear(String json) throws Exception {
        return mockMvc.perform(post("/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }

    private ResultActions actualizarStock(String nombre, String json) throws Exception {
        return mockMvc.perform(put("/productos/{nombre}/stock", nombre)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }

    private ResultActions retirarStock(String nombre, String json) throws Exception {
        return mockMvc.perform(post("/productos/{nombre}/stock/retiros", nombre)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }
}
