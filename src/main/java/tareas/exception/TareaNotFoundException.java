package tareas.exception;

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
