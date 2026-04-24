package tareas.exception;

/**
 * EXCEPCIÓN PROPIA DEL DOMINIO — TareaNotFoundException
 *
 * CAMBIO vs tu versión:
 * - Tu versión usaba RuntimeException genérico directamente en el Service.
 *   El problema es que RuntimeException puede significar CUALQUIER error,
 *   y el ExceptionHandler no puede distinguir si es un "no encontrado" (404)
 *   o un error de programación (500).
 *
 * - Al crear nuestra propia excepción, el ExceptionHandler puede capturarla
 *   ESPECÍFICAMENTE y devolver siempre un 404, sin afectar otros errores.
 *
 * ¿POR QUÉ extiende RuntimeException y no Exception?
 * - Las excepciones que extienden Exception son "checked" → Java te obliga
 *   a declarar "throws TareaNotFoundException" en cada método que la lanza,
 *   lo que hace el código muy verboso.
 * - Las que extienden RuntimeException son "unchecked" → no necesitas declararlas,
 *   se propagan solos hasta que alguien las captura (@ExceptionHandler).
 */
public class TareaNotFoundException extends RuntimeException {

    private final int id;

    public TareaNotFoundException(String message) {
        super(message);
        this.id = -1;
    }

    public TareaNotFoundException(String message, int id) {
        super(message);
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
