package codesmell.invoice.dao;

import java.util.List;

public interface InvoiceDao {

	public List<InvoiceMetaData> findInvoiceByDestination(InvoiceActor dest, String trailer);

	public String retrieveInvoiceDocumentByIdentifier(String invoiceId);

}
