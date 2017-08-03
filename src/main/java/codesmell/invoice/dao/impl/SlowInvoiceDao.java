package codesmell.invoice.dao.impl;

import codesmell.invoice.dao.InvoiceActor;
import codesmell.invoice.dao.InvoiceDao;
import codesmell.invoice.dao.InvoiceDaoException;
import codesmell.invoice.dao.InvoiceMetaData;
import codesmell.invoice.dao.Pack;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SlowInvoiceDao implements InvoiceDao {

    @Autowired
    ObjectMapper jsonMapper;

    @Override
    public List<InvoiceMetaData> findInvoiceByDestination(InvoiceActor dest, String trailer) {
        List<InvoiceMetaData> list = new ArrayList<>();

        String storeNumber = (dest != null ? dest.getActorName() : "");

        int count = 0;
        while (count < 5) {
            count++;
            InvoiceMetaData meta = InvoiceMetaData.builder().withInternalId(UUID.randomUUID().toString())
                    .withDocumentNumber(new Double(Math.random()).toString()).shippingOnTrailer(trailer).suppliedBy().named("Marvel").as("DC").end()
                    .beingSentTo().named(storeNumber).as("STORE").end().build();

            list.add(meta);
        }

        return list;
    }

    @Override
    public String retrieveInvoiceDocumentByIdentifier(String invoiceId) {
        try {
            Pack pack = Pack.builder()
                .identifiedBy(invoiceId)
                .packNumber("pack")
                .beingTransportedOn("trailer")
                .suppliedBy("Marvel", "DC")
                .receivedBy("store", "STORE")
                .build();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }

            return jsonMapper.writeValueAsString(pack);
        } catch (IOException e) {
            throw new InvoiceDaoException();
        }
    }

}
