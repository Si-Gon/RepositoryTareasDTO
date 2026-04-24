package tareas;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import tareas.dto.TareaRequest;
import tareas.model.Tarea;
import tareas.repository.TareaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * PRUEBAS REST — TareasControllerTest (IE 1.3.3)
 *
 * CAMBIO vs tu versión:
 * - Tu versión tenía TareasApplicationTests vacío (solo arrancaba el contexto).
 * - Estas pruebas verifican CADA endpoint con casos reales.
 *
 * ¿QUÉ ES MockMvc?
 * MockMvc simula un servidor HTTP dentro de los tests.
 * No necesitas levantar la app — Spring la simula internamente.
 * Puedes hacer requests y verificar respuestas sin abrir Postman.
 *
 * ¿QUÉ ES @SpringBootTest?
 * Carga el contexto completo de Spring para los tests.
 * Todos los @Service, @Repository, @Controller son instanciados realmente.
 *
 * ¿QUÉ ES @AutoConfigureMockMvc?
 * Le dice a Spring que configure MockMvc automáticamente e inyéctalo.
 *
 * CÓMO LEER UNA PRUEBA:
 * mockMvc.perform(...)       → ejecuta el request
 *        .andExpect(...)     → verifica algo de la respuesta
 *
 * status().isOk()            → verifica que el código HTTP sea 200
 * jsonPath("$.data")         → verifica que en el JSON exista el campo "data"
 */
@SpringBootTest
@AutoConfigureMockMvc
class TareasControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TareaRepository tareaRepository;

    private ObjectMapper objectMapper;

    /**
     * @BeforeEach se ejecuta ANTES de cada test.
     * Limpiamos el almacén para que los tests no se afecten entre sí.
     * (Como hacemos flush en una base de datos de test).
     */
    @BeforeEach
    void setUp() {
        // Limpiamos el HashMap antes de cada prueba
        // Nota: en un proyecto real usaríamos una base de datos de test separada
        tareaRepository.obtenerTodas().forEach(t -> tareaRepository.eliminar(t.getId()));
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    // =========================================================
    // TEST: POST — Crear tarea
    // =========================================================

    @Test
    @DisplayName("POST /tareas → 201 Created con tarea válida")
    void crearTarea_valida_retorna201() throws Exception {
        TareaRequest request = new TareaRequest();
        request.setDescripcion("Preparar informe mensual");
        request.setPrioridad("ALTA");
        request.setResponsable("Juan Pérez");
        request.setFechaAsociada(LocalDate.now().plusDays(7));

        mockMvc.perform(post("/api/v1/tareas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())                    // 201
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.id").exists())          // id asignado
                .andExpect(jsonPath("$.data.descripcion")
                        .value("Preparar informe mensual"));
    }

    @Test
    @DisplayName("POST /tareas → 400 Bad Request con datos inválidos")
    void crearTarea_invalida_retorna400() throws Exception {
        TareaRequest request = new TareaRequest();
        // descripcion vacía → viola @NotBlank
        request.setDescripcion("");
        request.setPrioridad("INVALIDA"); // viola @Pattern
        request.setResponsable("J");     // viola @Size(min=2)
        request.setFechaAsociada(LocalDate.now().plusDays(1));

        mockMvc.perform(post("/api/v1/tareas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())  // 400
                .andExpect(jsonPath("$.campos").exists()); // errores de campo
    }

    // =========================================================
    // TEST: GET — Obtener tareas
    // =========================================================

    @Test
    @DisplayName("GET /tareas → 200 OK lista vacía")
    void getTodas_listaVacia_retorna200() throws Exception {
        mockMvc.perform(get("/api/v1/tareas"))
                .andExpect(status().isOk())           // 200
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(0)));
    }

    @Test
    @DisplayName("GET /tareas/{id} → 200 OK con tarea existente")
    void getTareaById_existente_retorna200() throws Exception {
        // Creamos una tarea directo en el repositorio para el test
        Tarea tarea = new Tarea();
        tarea.setDescripcion("Tarea de prueba");
        tarea.setPrioridad("MEDIA");
        tarea.setResponsable("Ana");
        tarea.setFechaAsociada(LocalDate.now().plusDays(3));
        Tarea guardada = tareaRepository.guardar(tarea);

        mockMvc.perform(get("/api/v1/tareas/" + guardada.getId()))
                .andExpect(status().isOk())           // 200
                .andExpect(jsonPath("$.data.id").value(guardada.getId()))
                .andExpect(jsonPath("$.data.descripcion").value("Tarea de prueba"));
    }

    @Test
    @DisplayName("GET /tareas/{id} → 404 Not Found con id inexistente")
    void getTareaById_noExiste_retorna404() throws Exception {
        mockMvc.perform(get("/api/v1/tareas/9999"))
                .andExpect(status().isNotFound())     // 404
                .andExpect(jsonPath("$.error").value("Recurso no encontrado"));
    }

    // =========================================================
    // TEST: PUT — Actualizar tarea
    // =========================================================

    @Test
    @DisplayName("PUT /tareas/{id} → 200 OK actualización exitosa")
    void actualizarTarea_existente_retorna200() throws Exception {
        Tarea tarea = new Tarea();
        tarea.setDescripcion("Tarea original");
        tarea.setPrioridad("BAJA");
        tarea.setResponsable("Carlos");
        tarea.setFechaAsociada(LocalDate.now().plusDays(5));
        Tarea guardada = tareaRepository.guardar(tarea);

        TareaRequest update = new TareaRequest();
        update.setDescripcion("Tarea actualizada");
        update.setPrioridad("ALTA");
        update.setResponsable("Carlos");
        update.setFechaAsociada(LocalDate.now().plusDays(10));

        mockMvc.perform(put("/api/v1/tareas/" + guardada.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.descripcion").value("Tarea actualizada"))
                .andExpect(jsonPath("$.data.prioridad").value("ALTA"));
    }

    // =========================================================
    // TEST: DELETE — Eliminar tarea
    // =========================================================

    @Test
    @DisplayName("DELETE /tareas/{id} → 204 No Content")
    void eliminarTarea_existente_retorna204() throws Exception {
        Tarea tarea = new Tarea();
        tarea.setDescripcion("Para eliminar");
        tarea.setPrioridad("BAJA");
        tarea.setResponsable("Luisa");
        tarea.setFechaAsociada(LocalDate.now().plusDays(1));
        Tarea guardada = tareaRepository.guardar(tarea);

        mockMvc.perform(delete("/api/v1/tareas/" + guardada.getId()))
                .andExpect(status().isNoContent()); // 204
    }

    @Test
    @DisplayName("DELETE /tareas/{id} → 404 si no existe")
    void eliminarTarea_noExiste_retorna404() throws Exception {
        mockMvc.perform(delete("/api/v1/tareas/9999"))
                .andExpect(status().isNotFound()); // 404
    }

    // =========================================================
    // TEST: TRANSFORMACIONES (IE 1.2.3)
    // =========================================================

    @Test
    @DisplayName("GET /tareas/filtrar?prioridad=ALTA → solo tareas ALTA")
    void filtrarPorPrioridad_retornaFiltradas() throws Exception {
        // Creamos tareas con distintas prioridades
        crearTareaEnRepo("Tarea alta 1", "ALTA", "Pedro");
        crearTareaEnRepo("Tarea media", "MEDIA", "Laura");
        crearTareaEnRepo("Tarea alta 2", "ALTA", "Pedro");

        mockMvc.perform(get("/api/v1/tareas/filtrar").param("prioridad", "ALTA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)));
    }

    @Test
    @DisplayName("GET /tareas/buscar?texto=informe → tareas con 'informe'")
    void buscarPorDescripcion_retornaCoincidencias() throws Exception {
        crearTareaEnRepo("Preparar informe mensual", "ALTA", "Luis");
        crearTareaEnRepo("Revisar informe anterior", "MEDIA", "Luis");
        crearTareaEnRepo("Reunión de equipo", "BAJA", "Ana");

        mockMvc.perform(get("/api/v1/tareas/buscar").param("texto", "informe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)));
    }

    // =========================================================
    // MÉTODO AUXILIAR DE TEST
    // =========================================================

    private void crearTareaEnRepo(String descripcion, String prioridad, String responsable) {
        Tarea t = new Tarea();
        t.setDescripcion(descripcion);
        t.setPrioridad(prioridad);
        t.setResponsable(responsable);
        t.setFechaAsociada(LocalDate.now().plusDays(5));
        tareaRepository.guardar(t);
    }
}
