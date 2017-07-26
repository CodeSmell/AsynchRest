package codesmell.invoice.rest;

import codesmell.foo.FooBar;
import codesmell.invoice.dao.InvoiceActorType;
import codesmell.invoice.dao.InvoiceMetaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class InvoiceController {

    @Autowired
    FooBar foobar;

    @RequestMapping(
            path = "/foo",
            method = RequestMethod.GET)
    @ResponseBody
    public String fooBar() {
        return "Hello " + foobar.getHello();
    }

    @RequestMapping(
            path = "/invoice/find",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<InvoiceMetaData>> findInvoice(
            @RequestParam(value = "storeNumber") String storeNumber,
            @RequestParam(value = "trailerNumber") String trailerNumber) {

        List<InvoiceMetaData> list = new ArrayList<>();

        InvoiceMetaData meta = InvoiceMetaData.builder()
                .withInternalId(UUID.randomUUID().toString())
                .withDocumentNumber(new Double(Math.random()).toString())
                .shippingOnTrailer(trailerNumber)
                .suppliedBy()
                    .named("Marvel")
                    .as(InvoiceActorType.VENDOR)
                .end()
                .beingSentTo()
                    .named(storeNumber)
                    .as(InvoiceActorType.STORE)
                .end()
                .build();

        list.add(meta);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

}
