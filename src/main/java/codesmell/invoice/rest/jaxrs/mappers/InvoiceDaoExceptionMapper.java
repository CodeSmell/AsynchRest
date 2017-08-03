package codesmell.invoice.rest.jaxrs.mappers;

import codesmell.invoice.dao.InvoiceDaoException;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Component
public class InvoiceDaoExceptionMapper implements ExceptionMapper<InvoiceDaoException> {

    @Override
    public Response toResponse(InvoiceDaoException e) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("").build();
    }

}
