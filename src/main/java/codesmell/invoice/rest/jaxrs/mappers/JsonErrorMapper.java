package codesmell.invoice.rest.jaxrs.mappers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.springframework.stereotype.Component;

import codesmell.invoice.rest.JsonErrorException;

@Provider
@Component
public class JsonErrorMapper implements ExceptionMapper<JsonErrorException> {

    @Override
    public Response toResponse(JsonErrorException e) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("").build();
    }

}
