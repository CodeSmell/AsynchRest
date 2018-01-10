package codesmell.invoice.rest.spring.sync;

import codesmell.invoice.dao.InvoiceDao;
import codesmell.invoice.dao.InvoiceMetaData;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class InvoiceControllerEmbeddedServerTest {

	private static final String BASE_URL = "/springmvc/invoice/find";
	
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    InvoiceDao mockDao;

    @After
    public void cleanUp() throws Exception {
        reset(mockDao);
    }

    @Test
    public void test_invoice() throws Exception {
        List<InvoiceMetaData> daoPackList = new ArrayList<>();
        daoPackList.add(InvoiceMetaData.builder().withInternalId("UUID-1").shippingOnTrailer("YT-1300").build());
        daoPackList.add(InvoiceMetaData.builder().withInternalId("UUID-2").shippingOnTrailer("YT-1300").build());
        daoPackList.add(InvoiceMetaData.builder().withInternalId("UUID-3").shippingOnTrailer("YT-1300").build());

        when(mockDao.findInvoiceByDestination(any(), any())).thenReturn(daoPackList);
        when(mockDao.retrieveInvoiceDocumentByIdentifier(any())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return "{\"packId\":\"" + args[0] + "\"}";
            }
        });

        String storeNum = "100";
        String trailerNum = "YT-1300";

        String uri = "http://localhost:" + port + BASE_URL + "?storeNumber={storeNum}&trailerNumber={trailerNum}";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, String.class, storeNum, trailerNum);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        DocumentContext dc = JsonPath.parse(response.getBody());
        assertEquals(new Integer(3), dc.read("$.length()"));
        assertNotNull(dc.read("$[0].packId"));
        assertNotNull(dc.read("$[2].packId"));
    }

    @Test
    public void test_invoice_none() throws Exception {
        String storeNum = "100";
        String trailerNum = "YT-1300";

        when(mockDao.findInvoiceByDestination(any(), any())).thenReturn(new ArrayList<InvoiceMetaData>());

        String uri = "http://localhost:" + port + BASE_URL + "?storeNumber={storeNum}&trailerNumber={trailerNum}";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, String.class, storeNum, trailerNum);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

}
