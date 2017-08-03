package codesmell.invoice.rest.jaxrs;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.ApplicationPath;

@Configuration
@ApplicationPath("/jaxrs")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(HelloJerseyController.class);
        register(JaxRsInvoiceController.class);
        register(JaxRsAsynchInvoiceController.class);
        packages("codesmell.invoice.rest.jaxrs.mappers");
    }
}