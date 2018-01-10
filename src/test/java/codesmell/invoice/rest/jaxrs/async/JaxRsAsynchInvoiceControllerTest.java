package codesmell.invoice.rest.jaxrs.async;

import codesmell.invoice.dao.InvoiceDao;
import codesmell.invoice.dao.InvoiceDaoException;
import codesmell.invoice.dao.InvoiceMetaData;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class JaxRsAsynchInvoiceControllerTest {
    String storeNum = "100";
    String trailerNum = "YT-1300";

    private final String INVOICE_PATH = "/invoice/find/async";

    @LocalServerPort
    int port;

    @MockBean
    InvoiceDao mockDao;

    @After
    public void cleanup() {
        reset(mockDao);
    }

    @Test
    public void test_find_invoice_jaxrsClient() throws InterruptedException, ExecutionException {
        List<InvoiceMetaData> daoPackList = new ArrayList<>();
        daoPackList.add(InvoiceMetaData.builder().withInternalId("UUID-1").shippingOnTrailer(trailerNum).build());
        daoPackList.add(InvoiceMetaData.builder().withInternalId("UUID-2").shippingOnTrailer(trailerNum).build());
        daoPackList.add(InvoiceMetaData.builder().withInternalId("UUID-3").shippingOnTrailer(trailerNum).build());

        when(mockDao.findInvoiceByDestination(any(), any())).thenReturn(daoPackList);
        when(mockDao.retrieveInvoiceDocumentByIdentifier(any())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return "{\"packId\":\"" + args[0] + "\"}";
            }
        });

        Client jaxRsClient = ClientBuilder.newClient();
        WebTarget webTarget = jaxRsClient.target("http://localhost:" + port + "/jaxrs");
        WebTarget helloTarget = webTarget.path(INVOICE_PATH);

        Response response = helloTarget
            .queryParam("storeNumber", storeNum)
            .queryParam("trailerNumber", trailerNum)
            .request()
            .accept(MediaType.APPLICATION_JSON)
            .get();

        assertEquals(200, response.getStatus());
        DocumentContext dc = JsonPath.parse(response.readEntity(String.class));
        assertEquals(new Integer(3), dc.read("$.length()"));
        assertNotNull(dc.read("$[0].packId"));
        assertNotNull(dc.read("$[2].packId"));
    }

    @Test
    public void test_find_invoice_jaxrsClient_not_found() throws InterruptedException, ExecutionException {
        when(mockDao.findInvoiceByDestination(any(), any())).thenReturn(new ArrayList<InvoiceMetaData>());

        Client jaxRsClient = ClientBuilder.newClient();
        WebTarget webTarget = jaxRsClient.target("http://localhost:" + port + "/jaxrs");
        WebTarget helloTarget = webTarget.path(INVOICE_PATH);

        Response response = helloTarget
            .queryParam("storeNumber", storeNum)
            .queryParam("trailerNumber", trailerNum)
            .request()
            .accept(MediaType.APPLICATION_JSON)
            .get();

        assertEquals(204, response.getStatus());
        assertEquals("", response.readEntity(String.class));
    }

    @Test
    public void test_find_invoice_jaxrsClient_dao_exception() throws InterruptedException, ExecutionException {
        when(mockDao.findInvoiceByDestination(any(), any())).thenThrow(new InvoiceDaoException());

        Client jaxRsClient = ClientBuilder.newClient();
        WebTarget webTarget = jaxRsClient.target("http://localhost:" + port + "/jaxrs");
        WebTarget helloTarget = webTarget.path(INVOICE_PATH);

        Response response = helloTarget
            .queryParam("storeNumber", storeNum)
            .queryParam("trailerNumber", trailerNum)
            .request()
            .accept(MediaType.APPLICATION_JSON)
            .get();

        assertEquals(500, response.getStatus());
        assertEquals("", response.readEntity(String.class));
    }

    @Test
    public void test_find_invoice_jaxrsClient_dao_pack_exception() throws InterruptedException, ExecutionException {
        List<InvoiceMetaData> daoPackList = new ArrayList<>();
        daoPackList.add(InvoiceMetaData.builder().withInternalId("UUID-1").shippingOnTrailer(trailerNum).build());
        daoPackList.add(InvoiceMetaData.builder().withInternalId("UUID-2").shippingOnTrailer(trailerNum).build());
        daoPackList.add(InvoiceMetaData.builder().withInternalId("UUID-3").shippingOnTrailer(trailerNum).build());

        when(mockDao.findInvoiceByDestination(any(), any())).thenReturn(daoPackList);
        when(mockDao.retrieveInvoiceDocumentByIdentifier(any())).thenThrow(new InvoiceDaoException());

        Client jaxRsClient = ClientBuilder.newClient();
        WebTarget webTarget = jaxRsClient.target("http://localhost:" + port + "/jaxrs");
        WebTarget helloTarget = webTarget.path(INVOICE_PATH);

        Response response = helloTarget
            .queryParam("storeNumber", storeNum)
            .queryParam("trailerNumber", trailerNum)
            .request()
            .accept(MediaType.APPLICATION_JSON)
            .get();

        assertEquals(500, response.getStatus());
        assertEquals("", response.readEntity(String.class));
    }

}
