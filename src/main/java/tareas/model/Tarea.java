package tareas.model;

import java.time.LocalDate;

/**
 * MODELO DE DOMINIO — representa una Tarea en el sistema.
 *
 * CAMBIO vs tu versión:
 * - Se eliminaron @Entity, @Table, @GeneratedValue → ya no necesitamos base de datos.
 *   La rúbrica pide persistencia en MEMORIA (List/Map), no MySQL.
 * - Se eliminaron @Positive en campos String → @Positive es para números (int, double).
 *   Usarlo en un String no tiene sentido y puede lanzar error en runtime.
 * - El nombre de la clase es singular "Tarea" (no "Tareas") → una clase representa
 *   UNA entidad, la colección de tareas se maneja en el repositorio.
 * - Se eliminó Lombok (@Data, @AllArgsConstructor) para que veas el código real
 *   que Lombok generaba automáticamente, así entiendes qué hace cada parte.
 * - El id ahora es Integer (objeto) en vez de int (primitivo) → permite null,
 *   lo que es útil para detectar si una tarea aún no tiene id asignado.
 */
public class Tarea {

    private Integer id;
    private String descripcion;
    private String prioridad;   // "ALTA", "MEDIA", "BAJA"
    private String responsable;
    private LocalDate fechaAsociada;
    private boolean completada;

    // Constructor vacío — necesario para que Jackson pueda deserializar el JSON
    // del @RequestBody. Sin este constructor, Spring no puede crear el objeto.
    public Tarea() {}

    // Constructor completo — útil cuando queremos crear una tarea con todos los datos
    public Tarea(Integer id, String descripcion, String prioridad,
                 String responsable, LocalDate fechaAsociada, boolean completada) {
        this.id = id;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.responsable = responsable;
        this.fechaAsociada = fechaAsociada;
        this.completada = completada;
    }

    // --- GETTERS Y SETTERS ---
    // Spring los necesita para serializar el objeto a JSON (respuesta)
    // y para deserializar el JSON recibido (request body).

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getPrioridad() { return prioridad; }
    public void setPrioridad(String prioridad) { this.prioridad = prioridad; }

    public String getResponsable() { return responsable; }
    public void setResponsable(String responsable) { this.responsable = responsable; }

    public LocalDate getFechaAsociada() { return fechaAsociada; }
    public void setFechaAsociada(LocalDate fechaAsociada) { this.fechaAsociada = fechaAsociada; }

    public boolean isCompletada() { return completada; }
    public void setCompletada(boolean completada) { this.completada = completada; }
}
