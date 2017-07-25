package codesmell.invoice.rest;

import codesmell.foo.FooBar;
import codesmell.invoice.dao.InvoiceActorType;
import codesmell.invoice.dao.InvoiceMetaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class InvoiceController {

    @Autowired
    FooBar foobar;

    @RequestMapping("/foo")
    public String fooBar() {
        return "Hello " + foobar.getHello();
    }

    @RequestMapping("/find")
    public InvoiceMetaData findInvoice(
            @RequestParam(value = "storeNumber") String storeNumber,
            @RequestParam(value = "trailerNumber") String trailerNumber) {

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

        return meta;
    }

}
