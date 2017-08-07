package codesmell.invoice.rest.jaxrs;

import codesmell.invoice.rest.jaxrs.mappers.InvoiceDaoExceptionMapper;
import codesmell.invoice.rest.jaxrs.mappers.InvoiceNotFoundMapper;
import codesmell.invoice.rest.jaxrs.mappers.MissingRequiredParameterMapper;
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
        register(InvoiceDaoExceptionMapper.class);
        register(InvoiceNotFoundMapper.class);
        register(MissingRequiredParameterMapper.class);
        //packages("codesmell.invoice.rest.jaxrs.mappers");
    }
}