package codesmell.invoice.rest.jaxrs;

import codesmell.invoice.dao.InvoiceActor;
import codesmell.invoice.dao.InvoiceDao;
import codesmell.invoice.dao.InvoiceMetaData;
import codesmell.invoice.rest.InvoiceNotFoundException;
import codesmell.invoice.rest.MissingRequiredParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Path("/")
@Component
public class JaxRsAsynchInvoiceController {
    private static final Logger LOGGER = LoggerFactory.getLogger(JaxRsAsynchInvoiceController.class);

    @Autowired
    InvoiceDao dao;

    @GET
    @Path("/invoice/find/async")
    @Produces(MediaType.APPLICATION_JSON)
    public String findInvoice(
//            @Suspended AsyncResponse asyncResponse,
            @QueryParam(value = "storeNumber") String storeNumber,
            @QueryParam(value = "trailerNumber") String trailerNumber) {

        LOGGER.debug("start: async.invoice.find(" + storeNumber + "," + trailerNumber +")");

//        asyncResponse.setTimeout(10, TimeUnit.SECONDS);
//        asyncResponse.setTimeoutHandler(ar -> ar.resume(Response.status(Response.Status.REQUEST_TIMEOUT).build()));

        List<InvoiceMetaData> list = this.retrieveInvoices(storeNumber, trailerNumber);

        if (list == null || list.size() == 0) {
            throw new InvoiceNotFoundException();
        } else {
            // asynchronously process the list of packs
            List<CompletableFuture<String>> packJsonFutures =
                    list.stream()
                        .map(packMeta -> packMeta.getInternalId())
                        .map(packId -> CompletableFuture.supplyAsync(() -> dao.retrieveInvoiceDocumentByIdentifier(packId)))
                        .collect(Collectors.toList());

            String packJson = packJsonFutures.stream()
                .map(future -> future.join())
                .collect(Collectors.joining(",", "[", "]"));

            return packJson;
        }
        //LOGGER.debug("end main thread: async.invoice.find(" + storeNumber + "," + trailerNumber +")");
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
