package codesmell.invoice.rest.jaxrs;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import codesmell.invoice.rest.jaxrs.async.JaxRsAsyncStreamInvoiceController;
import codesmell.invoice.rest.jaxrs.async.JaxRsAsynchInvoiceController;
import codesmell.invoice.rest.jaxrs.hello.HelloJerseyController;
import codesmell.invoice.rest.jaxrs.mappers.InvoiceDaoExceptionMapper;
import codesmell.invoice.rest.jaxrs.mappers.InvoiceNotFoundMapper;
import codesmell.invoice.rest.jaxrs.mappers.MissingRequiredParameterMapper;
import codesmell.invoice.rest.jaxrs.sync.JaxRsInvoiceController;
import codesmell.invoice.rest.jaxrs.sync.JaxRsInvoiceStreamingController;

@Configuration
@ApplicationPath("/jaxrs")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(HelloJerseyController.class);
        register(JaxRsInvoiceController.class);
        register(JaxRsInvoiceStreamingController.class);
        register(JaxRsAsynchInvoiceController.class);
        register(JaxRsAsyncStreamInvoiceController.class);
        register(InvoiceDaoExceptionMapper.class);
        register(InvoiceNotFoundMapper.class);
        register(MissingRequiredParameterMapper.class);
        //packages("codesmell.invoice.rest.jaxrs.mappers");
    }
}