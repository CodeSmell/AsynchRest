package codesmell.invoice.rest.jaxrs.sync;

import codesmell.invoice.dao.InvoiceActor;
import codesmell.invoice.dao.InvoiceDao;
import codesmell.invoice.dao.InvoiceMetaData;
import codesmell.invoice.rest.InvoiceNotFoundException;
import codesmell.invoice.rest.JsonErrorException;
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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.function.Function;

@Path("/")
@Component
public class JaxRsInvoiceStreamingController {

	private static final Logger LOGGER = LoggerFactory.getLogger(JaxRsInvoiceStreamingController.class);

	@Autowired
	InvoiceDao dao;

	/**
	 * synchronous processing
	 * 
	 * @param storeNumber
	 * @param trailerNumber
	 * @return array of pack json
	 */
	@GET
	@Path("/invoice/find/stream")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findInvoice(@QueryParam(value = "storeNumber") String storeNumber,
			@QueryParam(value = "trailerNumber") String trailerNumber) {

		LOGGER.debug("synch/stream find.invoice() : start");

		List<InvoiceMetaData> list = this.retrieveInvoices(storeNumber, trailerNumber);

		StreamingOutput so = streamingResponse(list);
		
		return Response.ok(so).build();
	}

	protected StreamingOutput streamingResponse(List<InvoiceMetaData> packList) {
		StreamingOutput so = null;
		if (packList != null && !packList.isEmpty()) {
			try {
				so = this.streamingPackJson(packList, this::retrievePackJson);
			} catch (Exception e) {
				// error building streaming JSON
				throw new JsonErrorException();
			}
		} else {
			// nothing in the list
			throw new InvoiceNotFoundException();
		}
		return so;
	}
	
    protected String retrievePackJson(InvoiceMetaData pack) {
        return dao.retrieveInvoiceDocumentByIdentifier(pack.getInternalId());
    }

	protected StreamingOutput streamingPackJson(final List<InvoiceMetaData> packList,
			Function<InvoiceMetaData, String> getJson) {

		return new StreamingOutput() {
			@Override
			public void write(OutputStream os) throws IOException, WebApplicationException {
				Writer writer = new BufferedWriter(new OutputStreamWriter(os));
				// start JSON array
				writer.write("[");
				if (packList != null) {
					int packCount = packList.size();
					// write out each Pack Metadata JSON
					for (int i = 0; i < packCount; i++) {
						InvoiceMetaData pack = packList.get(i);
						// apply the function that was passed in
						String json = getJson.apply(pack);
						LOGGER.debug("retrieved JSON & writing to stream...");
						writer.write(json);
						if (i < packCount - 1) {
							writer.write(",");
						}
					}
				}
				// end JSON array
				writer.write("]");
				writer.flush();
			}
		};
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
