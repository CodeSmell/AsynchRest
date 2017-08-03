package codesmell.invoice.rest;

public class MissingRequiredParameterException extends RuntimeException {
    public MissingRequiredParameterException() {
        super();
    }

    public MissingRequiredParameterException(String message) {
        super(message);
    }

    public MissingRequiredParameterException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingRequiredParameterException(Throwable cause) {
        super(cause);
    }
}
