package codesmell.invoice.rest.spring;

import codesmell.invoice.dao.InvoiceActor;
import codesmell.invoice.dao.InvoiceDao;
import codesmell.invoice.dao.InvoiceMetaData;
import codesmell.invoice.rest.InvoiceNotFoundException;
import codesmell.invoice.rest.MissingRequiredParameterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class InvoiceController {

    @Autowired
    private InvoiceDao dao;

    @RequestMapping(
            path = "/invoice/find",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String findInvoice(
            @RequestParam(value = "storeNumber") String storeNumber,
            @RequestParam(value = "trailerNumber") String trailerNumber) {

        String jsonPacks = "";
        List<InvoiceMetaData> list = this.retrieveInvoices(storeNumber, trailerNumber);
        if (list == null || list.size() == 0) {
            throw new InvoiceNotFoundException();
        } else {
            jsonPacks = list.stream()
                .map(packMeta -> packMeta.getInternalId())
                .map(packId -> dao.retrieveInvoiceDocumentByIdentifier(packId))
                .collect(Collectors.joining(",", "[", "]"));
        }

        return jsonPacks;
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
