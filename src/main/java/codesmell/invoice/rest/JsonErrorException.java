package codesmell.invoice.rest;

public class JsonErrorException extends RuntimeException {
    public JsonErrorException() {
        super();
    }

    public JsonErrorException(String message) {
        super(message);
    }

    public JsonErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonErrorException(Throwable cause) {
        super(cause);
    }
}
