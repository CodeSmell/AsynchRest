package codesmell.invoice.rest.jaxrs.async;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.servlet.AsyncContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ManagedAsync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import codesmell.invoice.dao.InvoiceActor;
import codesmell.invoice.dao.InvoiceDao;
import codesmell.invoice.dao.InvoiceMetaData;
import codesmell.invoice.rest.InvoiceNotFoundException;
import codesmell.invoice.rest.MissingRequiredParameterException;

@Path("/")
@Component
public class JaxRsAsyncStreamInvoiceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(JaxRsAsynchInvoiceController.class);

    @Autowired
    InvoiceDao dao;
    
    @Autowired
    private ExecutorService execService;
    
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
    @Path("/invoice/find/async/stream")
    @Produces(MediaType.APPLICATION_JSON)
    @ManagedAsync
    public void findInvoice(
            @Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest servletRequest,
            @QueryParam(value = "storeNumber") String storeNumber,
            @QueryParam(value = "trailerNumber") String trailerNumber) throws IOException {

        LOGGER.debug("asynch.streaming find.invoice() : start");
        
        // retrieve the list of packs for the store/trailer
        List<InvoiceMetaData> list = this.retrieveInvoices(storeNumber, trailerNumber);
        
        // process the list of invoices/packs
        if (list == null || list.size() == 0) {
        		asyncResponse.resume(Response.noContent().build());
        } else {
            asyncResponse.setTimeout(10, TimeUnit.SECONDS);
            asyncResponse.setTimeoutHandler(ar -> ar.resume(Response.status(Response.Status.REQUEST_TIMEOUT).build()));

            // queue for pack JSON
            LinkedBlockingQueue<String> packQueue = new LinkedBlockingQueue<>();

            // process packs off the queue
            // until we have read off the same number as was in the list
            this.queueConsumer(list.size(), packQueue, servletRequest);

            // load the queue w/ pack JSON (future)
            this.getPackJsonFutureStream(list, packQueue, asyncResponse);
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
    
    /**
     * process list of meta data asynchronously
     * @param list
     * @param queue
     * @param asyncResponse
     */
    protected void getPackJsonFutureStream(List<InvoiceMetaData> list, BlockingQueue<String> queue, AsyncResponse asyncResponse) {
        list.forEach(metaData -> this.futurePutJsonOnQueue(metaData.getInternalId(), queue, asyncResponse));
    }

    protected boolean futurePutJsonOnQueue(String invoiceId, BlockingQueue<String> queue, AsyncResponse asyncResponse) {
        CompletableFuture
            .supplyAsync(() -> dao.retrieveInvoiceDocumentByIdentifier(invoiceId), execService)
            .thenAccept(invJson -> {
            		LOGGER.debug("retrieved JSON with invoiceId{}", invoiceId);
            		queue.offer(invJson);
            })
            .exceptionally(e -> {
            		asyncResponse.resume(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("").build());
                return null;
            });

        return true;
    }
    
    protected void queueConsumer(int packListSize, BlockingQueue<String> queue, HttpServletRequest servletRequest) throws IOException {
        LOGGER.debug("Queue Consumer...");

        AsyncContext asyncContext = servletRequest.getAsyncContext();
        ServletOutputStream outStream = asyncContext.getResponse().getOutputStream();

        ExecutorService queueService = Executors.newSingleThreadExecutor();
        queueService.execute(() -> this.queueExecute(packListSize, queue, outStream, asyncContext, queueService));
    }
    
	protected void queueExecute(int listSize, 
			BlockingQueue<String> queue, 
			ServletOutputStream outStream,
			AsyncContext asyncContext, 
			ExecutorService queueService) {
		
		try {
			int numberInvoicesProcessed = 0;
			while (numberInvoicesProcessed < listSize) {
				String invJson = queue.poll(1, TimeUnit.SECONDS);
				if (invJson != null) {
					// beginning of the array of JSON
					if (numberInvoicesProcessed == 0) {
						outStream.write("[".getBytes());
					}
					
					// write out JSON document
					outStream.write(invJson.getBytes());
					
					// end of JSON array 
					// or separate with a comma
					if (numberInvoicesProcessed == (listSize - 1)) {
						outStream.write("]".getBytes());
					} else {
						outStream.write(",".getBytes());
					}
					numberInvoicesProcessed++;
				}
				Thread.yield();
			}
			LOGGER.debug("asynch.streaming find.invoice(): done");
			outStream.close();
		} catch (Exception e) {
			LOGGER.warn(e.getMessage(), e);
		} finally {
			asyncContext.complete();
			queueService.shutdown();
		}
	}
}
