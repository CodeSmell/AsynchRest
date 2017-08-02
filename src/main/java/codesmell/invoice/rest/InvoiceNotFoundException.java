package codesmell.invoice.rest;

public class InvoiceNotFoundException extends RuntimeException {
    public InvoiceNotFoundException() {
        super();
    }

    public InvoiceNotFoundException(String message) {
        super(message);
    }

    public InvoiceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvoiceNotFoundException(Throwable cause) {
        super(cause);
    }
}
