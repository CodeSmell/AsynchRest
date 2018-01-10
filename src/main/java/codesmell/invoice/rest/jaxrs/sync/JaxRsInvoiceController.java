package codesmell.invoice.rest.jaxrs.sync;

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
import java.util.stream.Collectors;

@Path("/")
@Component
public class JaxRsInvoiceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(JaxRsInvoiceController.class);

    @Autowired
    InvoiceDao dao;

    /**
     * synchronous processing
     * @param storeNumber
     * @param trailerNumber
     * @return array of pack json
     */
    @GET
    @Path("/invoice/find")
    @Produces(MediaType.APPLICATION_JSON)
    public String findInvoice(
            @QueryParam(value = "storeNumber") String storeNumber,
            @QueryParam(value = "trailerNumber") String trailerNumber) {

        LOGGER.debug("synch find.invoice() : start");

        List<InvoiceMetaData> list = this.retrieveInvoices(storeNumber, trailerNumber);

        if (list == null || list.size() == 0) {
            throw new InvoiceNotFoundException();
        } else {
            // process the list of packs
            return list.stream()
                    .map(packMeta -> packMeta.getInternalId())
                    .map(packId -> dao.retrieveInvoiceDocumentByIdentifier(packId))
                    .collect(Collectors.joining(",", "[", "]"));
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
