package codesmell.invoice.rest.jaxrs.async;

import codesmell.invoice.dao.InvoiceActor;
import codesmell.invoice.dao.InvoiceDao;
import codesmell.invoice.dao.InvoiceMetaData;
import codesmell.invoice.rest.InvoiceNotFoundException;
import codesmell.invoice.rest.MissingRequiredParameterException;
import org.glassfish.jersey.server.ManagedAsync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Path("/")
@Component
public class JaxRsAsynchInvoiceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(JaxRsAsynchInvoiceController.class);

    @Autowired
    InvoiceDao dao;

    /**
     * uses {@link CompletableFuture} to process pack DAO JSON retrieval in parallel
     * while not necessary, it also takes advantage of JAX-RS async capabilities
     * to release the main thread that accepted the request (which is managed
     * automatically by adding the {@link ManagedAsync} annotation.
     * @param storeNumber
     * @param trailerNumber
     * @return array of pack json
     */
    @GET
    @Path("/invoice/find/async")
    @Produces(MediaType.APPLICATION_JSON)
    @ManagedAsync
    public void findInvoice(
            @Suspended final AsyncResponse asyncResponse,
            @QueryParam(value = "storeNumber") String storeNumber,
            @QueryParam(value = "trailerNumber") String trailerNumber) {

        LOGGER.debug("asynch find.invoice() : start");

        // retrieve the list of packs for the store/trailer
        List<InvoiceMetaData> list = this.retrieveInvoices(storeNumber, trailerNumber);

        if (list == null || list.size() == 0) {
            throw new InvoiceNotFoundException();
        } else {
            // set the time out
            asyncResponse.setTimeout(10, TimeUnit.SECONDS);
            asyncResponse.setTimeoutHandler(ar -> ar.resume(Response.status(Response.Status.REQUEST_TIMEOUT).build()));
            
            // asynchronously process the list of packs
            List<CompletableFuture<String>> packJsonFutures =
                    list.stream()
                        .map(packMeta -> packMeta.getInternalId())
                        .map(packId -> CompletableFuture
                                .supplyAsync(() -> dao.retrieveInvoiceDocumentByIdentifier(packId))
                                .exceptionally(e -> {
                                    asyncResponse.resume(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("").build());
                                    return null;
                                }))
                        .collect(Collectors.toList());

            String packJson = packJsonFutures.stream()
                .map(future -> future.join())
                .collect(Collectors.joining(",", "[", "]"));

            asyncResponse.resume(Response.status(Response.Status.OK).entity(packJson).build());

            LOGGER.debug("asynch find.invoice() : end");
        }
    }

    protected List<InvoiceMetaData> retrieveInvoices(String storeNumber, String trailerNumber) {

        if (StringUtils.isEmpty(storeNumber)) {
            throw new MissingRequiredParameterException();
        } else if (storeNumber.equals("NONE")) {
            throw new InvoiceNotFoundException();
        }

        return dao.findInvoiceByDestination(InvoiceActor.builder().named(storeNumber).build(), trailerNumber);
    }
}
