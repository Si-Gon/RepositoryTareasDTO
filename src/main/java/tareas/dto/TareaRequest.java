package tareas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.FutureOrPresent;

import java.time.LocalDate;

/**
 * DTO (Data Transfer Object) — TareaRequest
 *
 * ¿QUÉ ES UN DTO Y POR QUÉ EXISTE?
 * El DTO es el objeto que viaja entre el cliente y el servidor en el cuerpo
 * del request (POST, PUT). Es DISTINTO al modelo de dominio (Tarea).
 *
 * Razón principal de separarlos:
 * 1. El cliente NO debe poder enviar el "id" cuando crea una tarea — el id
 *    lo asigna el sistema. Si usáramos la clase Tarea directamente, el cliente
 *    podría mandar cualquier id y sobreescribir registros.
 * 2. Las validaciones (@NotBlank, @Size) son reglas del INPUT del usuario,
 *    no del modelo de dominio en sí.
 * 3. Puedes tener distintos DTOs para crear vs actualizar si los campos difieren.
 *
 * CAMBIO vs tu versión:
 * - Tu versión usaba la misma clase Tareas para todo (modelo + request body).
 *   Eso mezcla responsabilidades y es un problema de diseño.
 * - @Positive en Strings fue reemplazado por @Size y @NotBlank (correcto para texto).
 * - @Pattern valida que prioridad solo acepte valores permitidos.
 */
public class TareaRequest {

    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(min = 3, max = 200, message = "La descripción debe tener entre 3 y 200 caracteres")
    private String descripcion;

    @NotBlank(message = "La prioridad no puede estar vacía")
    @Pattern(regexp = "ALTA|MEDIA|BAJA",
             message = "La prioridad debe ser: ALTA, MEDIA o BAJA")
    private String prioridad;

    @NotBlank(message = "El responsable no puede estar vacío")
    @Size(min = 2, max = 100, message = "El responsable debe tener entre 2 y 100 caracteres")
    private String responsable;

    @NotNull(message = "La fecha asociada no puede ser nula")
    @FutureOrPresent(message = "La fecha debe ser hoy o en el futuro")
    private LocalDate fechaAsociada;

    private boolean completada = false;

    // Constructor vacío necesario para Jackson
    public TareaRequest() {}

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
