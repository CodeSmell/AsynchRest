package codesmell.invoice.rx;

import codesmell.invoice.dao.InvoiceActorType;
import codesmell.invoice.dao.InvoiceMetaData;

public class InvoiceData {

	public static InvoiceMetaData getMeta(String id, String destName, String trailer) {

		
		InvoiceMetaData meta = InvoiceMetaData.builder()
				.withInternalId(id)
				.withDocumentNumber(new Double(Math.random()).toString())
				.shippingOnTrailer(trailer)
				.suppliedBy()
					.named("Marvel")
					.as(InvoiceActorType.VENDOR)
					.end()
				.beingSentTo()
					.named(destName)
					.as(InvoiceActorType.STORE)
					.end()
				.build();
		
		return meta;
	}
	
	public static String getJsonDoc(String id) {
		String jsonResponse = null;
		
		if (id != null) {
			if ("1".equals(id)) {
				jsonResponse = jsonBuilder(id, "bar");
			} else {
				jsonResponse = jsonBuilder(id, "fighters");
			}
			
		}
		
		return jsonResponse;	
	}
	
	public static String jsonBuilder(String id, String foo) {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"invoice\": {");
		sb.append("\"id\": \"").append(id).append("\",");
		sb.append("\"foo\": \"").append(foo).append("\"");
		sb.append("}");
		sb.append("}");
		return sb.toString();
	}
}
