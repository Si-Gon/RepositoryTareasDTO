package tareas.repository;

import tareas.model.Tarea;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REPOSITORIO EN MEMORIA — almacena las tareas en un HashMap.
 *
 * CAMBIO vs tu versión (el más importante de todo el proyecto):
 * - Tu versión extendía JpaRepository y usaba MySQL con JDBC.
 *   La rúbrica pide explícitamente "persistencia en memoria" con List/Map.
 * - Esta clase NO extiende nada de JPA. Es un @Repository simple que
 *   maneja directamente un HashMap<Integer, Tarea>.
 *
 * ¿POR QUÉ HashMap Y NO ArrayList?
 * - HashMap<Integer, Tarea> permite buscar una tarea por id en O(1)
 *   (tiempo constante, muy rápido), sin recorrer toda la lista.
 * - ArrayList requeriría recorrer todos los elementos para buscar por id → O(n).
 * - La "llave" (key) del Map es el id, el "valor" (value) es la Tarea.
 *
 * ¿POR QUÉ "static"?
 * - El Map y el contador son static para que sean compartidos entre todas las
 *   instancias del repositorio (aunque Spring solo crea una por ser @Repository).
 *   Simula una "base de datos" que persiste mientras corre la aplicación.
 */
@Repository
public class TareaRepository {

    // Aquí viven las tareas mientras la aplicación está corriendo.
    // Cuando reinicias la app, se pierden — eso es "en memoria".
    private static final Map<Integer, Tarea> almacen = new HashMap<>();

    // Contador para generar ids únicos, como un auto_increment de SQL.
    private static int contadorId = 1;

    /**
     * Guarda una tarea nueva o actualiza una existente.
     * Si la tarea no tiene id → es nueva, se le asigna uno.
     * Si ya tiene id → se sobreescribe (actualización).
     */
    public Tarea guardar(Tarea tarea) {
        if (tarea.getId() == null) {
            // Es una tarea nueva: asignamos id y guardamos
            tarea.setId(contadorId++);
        }
        almacen.put(tarea.getId(), tarea);
        return tarea;
    }

    /**
     * Retorna todas las tareas como una List.
     * new ArrayList<>(almacen.values()) convierte los valores del Map a una lista.
     */
    public List<Tarea> obtenerTodas() {
        return new ArrayList<>(almacen.values());
    }

    /**
     * Busca una tarea por id.
     * Retorna Optional<Tarea> — un "contenedor" que puede tener la tarea o estar vacío.
     * Esto evita retornar null directamente y fuerza al que llama a manejar el caso
     * en que no existe.
     */
    public Optional<Tarea> buscarPorId(int id) {
        return Optional.ofNullable(almacen.get(id));
    }

    /**
     * Elimina una tarea por id.
     * Retorna true si existía y fue eliminada, false si no existía.
     */
    public boolean eliminar(int id) {
        return almacen.remove(id) != null;
    }

    /**
     * Verifica si existe una tarea con ese id.
     */
    public boolean existe(int id) {
        return almacen.containsKey(id);
    }
}
