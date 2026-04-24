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

@Service
public class TareaService {

    private final TareaRepository tareaRepository;

    public TareaService(TareaRepository tareaRepository) {
        this.tareaRepository = tareaRepository;
    }

    public List<Tarea> obtenerTodas() {
        return tareaRepository.obtenerTodas();
    }

    public Tarea obtenerPorId(int id) {
        return tareaRepository.buscarPorId(id)
                .orElseThrow(() -> new TareaNotFoundException("No existe tarea con id: " + id));
    }

    public Tarea crear(TareaRequest request) {
        Tarea tarea = new Tarea();
        tarea.setDescripcion(request.getDescripcion());
        tarea.setPrioridad(request.getPrioridad().toUpperCase());
        tarea.setResponsable(request.getResponsable());
        tarea.setFechaAsociada(request.getFechaAsociada());
        tarea.setCompletada(request.isCompletada());
        return tareaRepository.guardar(tarea);
    }

    public Tarea actualizar(int id, TareaRequest request) {
        if (!tareaRepository.existe(id)) {
            throw new TareaNotFoundException("No existe tarea con id: " + id);
        }
        Tarea tarea = new Tarea();
        tarea.setId(id);   
        tarea.setDescripcion(request.getDescripcion());
        tarea.setPrioridad(request.getPrioridad().toUpperCase());
        tarea.setResponsable(request.getResponsable());
        tarea.setFechaAsociada(request.getFechaAsociada());
        tarea.setCompletada(request.isCompletada());
        return tareaRepository.guardar(tarea);
    }

    public void eliminar(int id) {
        if (!tareaRepository.eliminar(id)) {
            throw new TareaNotFoundException("No existe tarea con id: " + id);
        }
    }

    public List<Tarea> filtrarPorPrioridad(String prioridad) {
        return tareaRepository.obtenerTodas()
                .stream()
                .filter(t -> t.getPrioridad().equalsIgnoreCase(prioridad))
                .collect(Collectors.toList());
    }

    public List<Tarea> buscarPorDescripcion(String texto) {
        return tareaRepository.obtenerTodas()
                .stream()
                .filter(t -> t.getDescripcion().toLowerCase().contains(texto.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Tarea> ordenarPorFecha() {
        return tareaRepository.obtenerTodas()
                .stream()
                .sorted(Comparator.comparing(Tarea::getFechaAsociada))
                .collect(Collectors.toList());
    }

    public Map<String, List<Tarea>> agruparPorPrioridad() {
        return tareaRepository.obtenerTodas()
                .stream()
                .collect(Collectors.groupingBy(Tarea::getPrioridad));
    }

    public List<Tarea> filtrarPorResponsable(String nombre) {
        return tareaRepository.obtenerTodas()
                .stream()
                .filter(t -> t.getResponsable().equalsIgnoreCase(nombre))
                .collect(Collectors.toList());
    }
}
