package tareas.service;

import tareas.dto.TareaRequest;
import tareas.exception.TareaNotFoundException;
import tareas.model.Tarea;
import tareas.repository.TareaRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * CAPA DE SERVICIO — contiene TODA la lógica del negocio.
 *
 * CAMBIO vs tu versión:
 * - Tu Service era casi un "pass-through" del Repository (llamaba directamente
 *   a JPA sin lógica propia). Un Service real debe contener reglas de negocio.
 * - Se agregaron transformaciones de colecciones (filtrar, buscar, ordenar, agrupar)
 *   que la rúbrica IE 1.2.3 exige y que faltaban completamente.
 * - Se lanza TareaNotFoundException (excepción propia) en vez de RuntimeException
 *   genérico, lo que permite al ExceptionHandler devolver un 404 correcto.
 * - El Service recibe un DTO (TareaRequest) y construye el modelo (Tarea),
 *   separando las responsabilidades correctamente.
 *
 * ¿QUÉ SON LOS STREAMS?
 * Los streams (Java 8+) permiten procesar colecciones de forma declarativa.
 * En vez de escribir un for-loop, describes QUÉ quieres hacer:
 *   lista.stream()          → abre el stream
 *        .filter(...)       → filtra elementos
 *        .sorted(...)       → ordena
 *        .collect(...)      → cierra y convierte de vuelta a List
 */
@Service
public class TareaService {

    private final TareaRepository tareaRepository;

    // INYECCIÓN POR CONSTRUCTOR — mejor práctica que @Autowired en el campo.
    // Razón: hace explícito que el Service NECESITA el Repository para funcionar,
    // y facilita las pruebas (puedes pasar un mock fácilmente).
    public TareaService(TareaRepository tareaRepository) {
        this.tareaRepository = tareaRepository;
    }

    // =========================================================
    // CRUD BÁSICO
    // =========================================================

    /** Retorna todas las tareas sin filtro. */
    public List<Tarea> obtenerTodas() {
        return tareaRepository.obtenerTodas();
    }

    /** Busca una tarea por id. Lanza excepción si no existe → el Handler devuelve 404. */
    public Tarea obtenerPorId(int id) {
        return tareaRepository.buscarPorId(id)
                .orElseThrow(() -> new TareaNotFoundException("No existe tarea con id: " + id));
    }

    /**
     * Crea una tarea nueva a partir del DTO.
     *
     * CAMBIO IMPORTANTE: el Service convierte el DTO → Modelo aquí.
     * El Controller no sabe nada de cómo se construye una Tarea.
     */
    public Tarea crear(TareaRequest request) {
        Tarea tarea = new Tarea();
        tarea.setDescripcion(request.getDescripcion());
        tarea.setPrioridad(request.getPrioridad().toUpperCase());
        tarea.setResponsable(request.getResponsable());
        tarea.setFechaAsociada(request.getFechaAsociada());
        tarea.setCompletada(request.isCompletada());
        // id es null → el Repository le asignará uno nuevo
        return tareaRepository.guardar(tarea);
    }

    /**
     * Actualiza una tarea existente.
     * Primero verifica que exista (lanza 404 si no), luego actualiza.
     */
    public Tarea actualizar(int id, TareaRequest request) {
        // Verificamos que exista antes de actualizar
        if (!tareaRepository.existe(id)) {
            throw new TareaNotFoundException("No existe tarea con id: " + id);
        }
        Tarea tarea = new Tarea();
        tarea.setId(id);   // id fijo: viene de la URL, no del body
        tarea.setDescripcion(request.getDescripcion());
        tarea.setPrioridad(request.getPrioridad().toUpperCase());
        tarea.setResponsable(request.getResponsable());
        tarea.setFechaAsociada(request.getFechaAsociada());
        tarea.setCompletada(request.isCompletada());
        return tareaRepository.guardar(tarea);
    }

    /**
     * Elimina una tarea. Lanza 404 si no existe.
     */
    public void eliminar(int id) {
        if (!tareaRepository.eliminar(id)) {
            throw new TareaNotFoundException("No existe tarea con id: " + id);
        }
    }

    // =========================================================
    // TRANSFORMACIONES DE COLECCIONES (IE 1.2.3)
    // Estas operaciones son las que faltaban completamente en tu proyecto.
    // =========================================================

    /**
     * FILTRAR — devuelve solo las tareas de una prioridad específica.
     * Ejemplo: GET /api/v1/tareas/filtrar?prioridad=ALTA
     *
     * .filter() recorre la lista y mantiene solo los elementos donde
     * la condición dentro del lambda es true.
     */
    public List<Tarea> filtrarPorPrioridad(String prioridad) {
        return tareaRepository.obtenerTodas()
                .stream()
                .filter(t -> t.getPrioridad().equalsIgnoreCase(prioridad))
                .collect(Collectors.toList());
    }

    /**
     * BUSCAR — devuelve tareas cuya descripción contiene el texto buscado.
     * Ejemplo: GET /api/v1/tareas/buscar?texto=reunion
     *
     * .toLowerCase() en ambos lados hace la búsqueda case-insensitive.
     */
    public List<Tarea> buscarPorDescripcion(String texto) {
        return tareaRepository.obtenerTodas()
                .stream()
                .filter(t -> t.getDescripcion().toLowerCase().contains(texto.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * ORDENAR — devuelve todas las tareas ordenadas por fecha (más próxima primero).
     * Ejemplo: GET /api/v1/tareas/ordenadas
     *
     * Comparator.comparing() crea un comparador basado en el campo fechaAsociada.
     * LocalDate implementa Comparable, así que sabe compararse con otras fechas.
     */
    public List<Tarea> ordenarPorFecha() {
        return tareaRepository.obtenerTodas()
                .stream()
                .sorted(Comparator.comparing(Tarea::getFechaAsociada))
                .collect(Collectors.toList());
    }

    /**
     * AGRUPAR (MAP) — devuelve un Map donde la clave es la prioridad
     * y el valor es la lista de tareas con esa prioridad.
     * Ejemplo: GET /api/v1/tareas/agrupadas
     *
     * Collectors.groupingBy() agrupa elementos de un stream por un criterio.
     * Resultado: { "ALTA": [...], "MEDIA": [...], "BAJA": [...] }
     */
    public Map<String, List<Tarea>> agruparPorPrioridad() {
        return tareaRepository.obtenerTodas()
                .stream()
                .collect(Collectors.groupingBy(Tarea::getPrioridad));
    }

    /**
     * FILTRAR por responsable — todas las tareas asignadas a una persona.
     * Ejemplo: GET /api/v1/tareas/responsable?nombre=Juan
     */
    public List<Tarea> filtrarPorResponsable(String nombre) {
        return tareaRepository.obtenerTodas()
                .stream()
                .filter(t -> t.getResponsable().equalsIgnoreCase(nombre))
                .collect(Collectors.toList());
    }
}
