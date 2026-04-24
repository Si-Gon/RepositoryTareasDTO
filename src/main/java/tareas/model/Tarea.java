package tareas.model;

import java.time.LocalDate;

public class Tarea {

    private Integer id;
    private String descripcion;
    private String prioridad; 
    private String responsable;
    private LocalDate fechaAsociada;
    private boolean completada;

    
    public Tarea() {}

    public Tarea(Integer id, String descripcion, String prioridad,
                 String responsable, LocalDate fechaAsociada, boolean completada) {
        this.id = id;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.responsable = responsable;
        this.fechaAsociada = fechaAsociada;
        this.completada = completada;
    }

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
