package tareas.repository;

import tareas.model.Tarea;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TareaRepository {

    private static final Map<Integer, Tarea> almacen = new HashMap<>();

    private static int contadorId = 1;


    public Tarea guardar(Tarea tarea) {
        if (tarea.getId() == null) {
            tarea.setId(contadorId++);
        }
        almacen.put(tarea.getId(), tarea);
        return tarea;
    }

    public List<Tarea> obtenerTodas() {
        return new ArrayList<>(almacen.values());
    }

    public Optional<Tarea> buscarPorId(int id) {
        return Optional.ofNullable(almacen.get(id));
    }

    public boolean eliminar(int id) {
        return almacen.remove(id) != null;
    }

    public boolean existe(int id) {
        return almacen.containsKey(id);
    }
}
