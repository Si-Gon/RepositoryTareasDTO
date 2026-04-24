package tareas.controller;

import jakarta.validation.Valid;
import tareas.dto.TareaRequest;
import tareas.model.Tarea;
import tareas.service.TareaService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CONTROLLER — TareaController
 *
 * RESPONSABILIDAD del Controller:
 * 1. Recibir el request HTTP
 * 2. Llamar al Service con los datos necesarios
 * 3. Devolver la respuesta con el código HTTP correcto
 * El Controller NO contiene lógica de negocio — eso va en el Service.
 *
 * CAMBIOS vs tu versión:
 *
 * 1. TODOS los métodos retornan ResponseEntity<?>
 *    - Antes: algunos retornaban Tarea o String directamente.
 *    - ResponseEntity permite controlar el código HTTP (200, 201, 404, etc.)
 *    - Sin ResponseEntity, Spring siempre devuelve 200 aunque no corresponda.
 *
 * 2. @Valid en @RequestBody
 *    - Faltaba completamente en tu versión.
 *    - Sin @Valid, las anotaciones @NotBlank/@Size del DTO son decoración inútil
 *      — Spring no las valida. Con @Valid sí las activa.
 *
 * 3. POST devuelve 201 Created con Location header
 *    - El estándar REST dice que POST exitoso → 201 + header Location con la URL
 *      del recurso creado. Tu versión devolvía 200.
 *
 * 4. DELETE devuelve 204 No Content
 *    - El estándar REST dice que DELETE exitoso → 204 (sin cuerpo).
 *    - Tu versión devolvía String, y además había un bug: el Service era void
 *      pero el Controller esperaba String.
 *
 * 5. Se eliminó el import de tareasRepository en el Controller
 *    - El Controller NUNCA debe hablar con el Repository directamente.
 *      Solo habla con el Service.
 *
 * 6. Nombre de clase en PascalCase: TareaController (no tareasController)
 *    - Convención estándar de Java para nombres de clases.
 */
@RestController
@RequestMapping("/api/v1/tareas")
public class TareaController {

    private final TareaService tareaService;

    // Inyección por constructor (mejor práctica que @Autowired en el campo)
    public TareaController(TareaService tareaService) {
        this.tareaService = tareaService;
    }

    // =========================================================
    // CRUD BÁSICO
    // =========================================================

    /**
     * GET /api/v1/tareas
     * Lista todas las tareas. Código 200 OK siempre (lista vacía es válida).
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getTodas() {
        List<Tarea> tareas = tareaService.obtenerTodas();
        return ResponseEntity.ok(construirRespuesta(HttpStatus.OK, tareas));
    }

    /**
     * GET /api/v1/tareas/{id}
     * Busca una tarea por id.
     * 200 OK si existe, 404 si no (el ExceptionHandler lo captura).
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable int id) {
        Tarea tarea = tareaService.obtenerPorId(id);
        return ResponseEntity.ok(construirRespuesta(HttpStatus.OK, tarea));
    }

    /**
     * POST /api/v1/tareas
     * Crea una tarea nueva.
     *
     * @Valid → activa las validaciones del DTO antes de llegar al Service.
     *
     * 201 Created → estándar REST para creación exitosa.
     * El header "Location" indica la URL del recurso recién creado.
     * Ejemplo: Location: http://localhost:8080/api/v1/tareas/3
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@Valid @RequestBody TareaRequest request) {
        Tarea nueva = tareaService.crear(request);

        // Construimos la URI del nuevo recurso para el header Location
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(nueva.getId())
                .toUri();

        return ResponseEntity
                .created(location)            // 201 Created + header Location
                .body(construirRespuesta(HttpStatus.CREATED, nueva));
    }

    /**
     * PUT /api/v1/tareas/{id}
     * Actualiza una tarea existente.
     * El id viene de la URL (no del body) → evita inconsistencias.
     * 200 OK si se actualizó, 404 si no existía.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizar(
            @PathVariable int id,
            @Valid @RequestBody TareaRequest request) {
        Tarea actualizada = tareaService.actualizar(id, request);
        return ResponseEntity.ok(construirRespuesta(HttpStatus.OK, actualizada));
    }

    /**
     * DELETE /api/v1/tareas/{id}
     * Elimina una tarea.
     * 204 No Content → estándar REST para delete exitoso (sin cuerpo en la respuesta).
     * 404 si no existía (capturado por el ExceptionHandler).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        tareaService.eliminar(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // =========================================================
    // ENDPOINTS DE TRANSFORMACIÓN DE COLECCIONES (IE 1.2.3)
    // =========================================================

    /**
     * GET /api/v1/tareas/filtrar?prioridad=ALTA
     * Filtra tareas por prioridad.
     */
    @GetMapping("/filtrar")
    public ResponseEntity<Map<String, Object>> filtrarPorPrioridad(
            @RequestParam String prioridad) {
        List<Tarea> resultado = tareaService.filtrarPorPrioridad(prioridad);
        return ResponseEntity.ok(construirRespuesta(HttpStatus.OK, resultado));
    }

    /**
     * GET /api/v1/tareas/buscar?texto=reunion
     * Busca tareas cuya descripción contiene el texto.
     */
    @GetMapping("/buscar")
    public ResponseEntity<Map<String, Object>> buscarPorDescripcion(
            @RequestParam String texto) {
        List<Tarea> resultado = tareaService.buscarPorDescripcion(texto);
        return ResponseEntity.ok(construirRespuesta(HttpStatus.OK, resultado));
    }

    /**
     * GET /api/v1/tareas/ordenadas
     * Retorna todas las tareas ordenadas por fecha (más próxima primero).
     */
    @GetMapping("/ordenadas")
    public ResponseEntity<Map<String, Object>> ordenarPorFecha() {
        List<Tarea> resultado = tareaService.ordenarPorFecha();
        return ResponseEntity.ok(construirRespuesta(HttpStatus.OK, resultado));
    }

    /**
     * GET /api/v1/tareas/agrupadas
     * Retorna tareas agrupadas por prioridad (Map<String, List<Tarea>>).
     */
    @GetMapping("/agrupadas")
    public ResponseEntity<Map<String, Object>> agruparPorPrioridad() {
        Map<String, List<Tarea>> agrupadas = tareaService.agruparPorPrioridad();
        return ResponseEntity.ok(construirRespuesta(HttpStatus.OK, agrupadas));
    }

    /**
     * GET /api/v1/tareas/responsable?nombre=Juan
     * Filtra tareas por responsable.
     */
    @GetMapping("/responsable")
    public ResponseEntity<Map<String, Object>> filtrarPorResponsable(
            @RequestParam String nombre) {
        List<Tarea> resultado = tareaService.filtrarPorResponsable(nombre);
        return ResponseEntity.ok(construirRespuesta(HttpStatus.OK, resultado));
    }

    // =========================================================
    // MÉTODO AUXILIAR
    // =========================================================

    /**
     * Construye la estructura de respuesta uniforme para todos los endpoints.
     * Tener un formato consistente en todas las respuestas es buena práctica REST.
     */
    private Map<String, Object> construirRespuesta(HttpStatus status, Object data) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("timestamp", LocalDateTime.now());
        respuesta.put("status", status.value());
        respuesta.put("data", data);
        return respuesta;
    }
}
