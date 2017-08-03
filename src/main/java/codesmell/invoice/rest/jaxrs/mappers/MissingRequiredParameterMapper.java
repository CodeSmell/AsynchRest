package codesmell.invoice.rest.jaxrs.mappers;

import codesmell.invoice.rest.MissingRequiredParameterException;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Component
public class MissingRequiredParameterMapper implements ExceptionMapper<MissingRequiredParameterException> {

    @Override
    public Response toResponse(MissingRequiredParameterException e) {
        return Response.status(Response.Status.BAD_REQUEST).entity("").build();
    }

}
