package codesmell.invoice.dao;

public class InvoiceDaoException extends RuntimeException {
    public InvoiceDaoException() {
        super();
    }

    public InvoiceDaoException(String message) {
        super(message);
    }

    public InvoiceDaoException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvoiceDaoException(Throwable cause) {
        super(cause);
    }
}
