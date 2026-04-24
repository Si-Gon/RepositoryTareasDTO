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

@RestController
@RequestMapping("/api/v1/tareas")
public class TareaController {

    private final TareaService tareaService;

    public TareaController(TareaService tareaService) {
        this.tareaService = tareaService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getTodas() {
        List<Tarea> tareas = tareaService.obtenerTodas();
        return ResponseEntity.ok(construirRespuesta(HttpStatus.OK, tareas));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable int id) {
        Tarea tarea = tareaService.obtenerPorId(id);
        return ResponseEntity.ok(construirRespuesta(HttpStatus.OK, tarea));
    }

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
                .created(location)           
                .body(construirRespuesta(HttpStatus.CREATED, nueva));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizar(
            @PathVariable int id,
            @Valid @RequestBody TareaRequest request) {
        Tarea actualizada = tareaService.actualizar(id, request);
        return ResponseEntity.ok(construirRespuesta(HttpStatus.OK, actualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        tareaService.eliminar(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @GetMapping("/filtrar")
    public ResponseEntity<Map<String, Object>> filtrarPorPrioridad(
            @RequestParam String prioridad) {
        List<Tarea> resultado = tareaService.filtrarPorPrioridad(prioridad);
        return ResponseEntity.ok(construirRespuesta(HttpStatus.OK, resultado));
    }

    @GetMapping("/buscar")
    public ResponseEntity<Map<String, Object>> buscarPorDescripcion(
            @RequestParam String texto) {
        List<Tarea> resultado = tareaService.buscarPorDescripcion(texto);
        return ResponseEntity.ok(construirRespuesta(HttpStatus.OK, resultado));
    }

    @GetMapping("/ordenadas")
    public ResponseEntity<Map<String, Object>> ordenarPorFecha() {
        List<Tarea> resultado = tareaService.ordenarPorFecha();
        return ResponseEntity.ok(construirRespuesta(HttpStatus.OK, resultado));
    }

    @GetMapping("/agrupadas")
    public ResponseEntity<Map<String, Object>> agruparPorPrioridad() {
        Map<String, List<Tarea>> agrupadas = tareaService.agruparPorPrioridad();
        return ResponseEntity.ok(construirRespuesta(HttpStatus.OK, agrupadas));
    }

    @GetMapping("/responsable")
    public ResponseEntity<Map<String, Object>> filtrarPorResponsable(
            @RequestParam String nombre) {
        List<Tarea> resultado = tareaService.filtrarPorResponsable(nombre);
        return ResponseEntity.ok(construirRespuesta(HttpStatus.OK, resultado));
    }

    private Map<String, Object> construirRespuesta(HttpStatus status, Object data) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("timestamp", LocalDateTime.now());
        respuesta.put("status", status.value());
        respuesta.put("data", data);
        return respuesta;
    }
}
