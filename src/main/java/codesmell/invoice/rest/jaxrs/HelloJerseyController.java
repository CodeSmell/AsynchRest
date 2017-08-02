package codesmell.invoice.rest.jaxrs;

import codesmell.foo.FooBar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
@Component
public class HelloJerseyController {

    @Autowired
    FooBar foobar;

    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String helloJersey() {
        return "Hello " + foobar.getHello() + " from JAX-RS (Jersey)";
    }
}