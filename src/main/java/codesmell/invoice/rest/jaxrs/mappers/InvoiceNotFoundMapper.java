package codesmell.invoice.rest.jaxrs.mappers;

import codesmell.invoice.rest.InvoiceNotFoundException;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Component
public class InvoiceNotFoundMapper implements ExceptionMapper<InvoiceNotFoundException> {

    @Override
    public Response toResponse(InvoiceNotFoundException e) {
        return Response.status(Response.Status.NO_CONTENT).entity("").build();
    }

}
