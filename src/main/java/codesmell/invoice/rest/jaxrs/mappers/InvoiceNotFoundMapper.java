package codesmell.invoice.rest.jaxrs.mappers;

import codesmell.invoice.rest.spring.InvoiceNotFoundException;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Component
public class InvoiceNotFoundMapper implements ExceptionMapper<InvoiceNotFoundException> {

    @Override
    public Response toResponse(InvoiceNotFoundException e) {
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

}
