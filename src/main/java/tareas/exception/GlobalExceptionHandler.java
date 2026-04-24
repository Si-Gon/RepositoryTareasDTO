package tareas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * MANEJADOR GLOBAL DE ERRORES — GlobalExceptionHandler
 *
 * CAMBIO vs tu versión:
 * - Tu versión no tenía nada de esto. Los errores lanzaban un 500 genérico
 *   con el stack trace de Java, que es feo, inseguro (expone internos) e incorrecto.
 *
 * ¿QUÉ ES @RestControllerAdvice?
 * Es una clase especial que Spring "intercepta" cuando cualquier Controller
 * lanza una excepción. En vez de que el error llegue al cliente como un 500 crudo,
 * esta clase lo captura y devuelve una respuesta JSON controlada y legible.
 *
 * Piénsalo como una "red de seguridad" global para todos tus controllers.
 *
 * ¿QUÉ ES @ExceptionHandler?
 * Anota un método que se ejecuta cuando se lanza una excepción específica.
 * Puedes tener uno por tipo de excepción.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Captura TareaNotFoundException → devuelve 404 Not Found
     * Se activa cuando el Service lanza: throw new TareaNotFoundException(...)
     */
    @ExceptionHandler(TareaNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleTareaNotFound(TareaNotFoundException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.NOT_FOUND.value());
        error.put("error", "Recurso no encontrado");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Captura MethodArgumentNotValidException → devuelve 400 Bad Request
     *
     * Esta excepción la lanza Spring automáticamente cuando @Valid detecta
     * que el request body no cumple las restricciones del DTO (@NotBlank, @Size, etc).
     *
     * Sin este handler, esa excepción resultaría en un 400 con un JSON enorme
     * y difícil de leer. Aquí la transformamos en algo limpio.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        // Recolectamos todos los errores de validación campo por campo
        Map<String, String> erroresCampos = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            erroresCampos.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Error de validación");
        error.put("campos", erroresCampos);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Captura cualquier otra excepción no prevista → devuelve 500
     * Es el "catch-all" de seguridad.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralError(Exception ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.put("error", "Error interno del servidor");
        error.put("message", "Ocurrió un error inesperado");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
