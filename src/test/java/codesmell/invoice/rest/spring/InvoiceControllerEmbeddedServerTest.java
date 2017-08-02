package codesmell.invoice.rest.spring;

import com.jayway.jsonpath.JsonPath;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class InvoiceControllerEmbeddedServerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void test_invoice() throws Exception {
        String uri = "http://localhost:" + port + "/invoice/find?storeNumber={storeNum}&trailerNumber={trailerNum}";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

        String storeNum = "100";
        String trailerNum = "YT-1300";

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, String.class, storeNum, trailerNum);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(new Integer(1), JsonPath.parse(response.getBody()).read("$.length()"));
        assertNotNull(JsonPath.parse(response.getBody()).read("$[0].internalId"));
        assertEquals(storeNum, JsonPath.parse(response.getBody()).read("$[0].invoiceDestination.actorName"));
        assertEquals(trailerNum, JsonPath.parse(response.getBody()).read("$[0].trailerNumber"));
    }

    @Test
    public void test_invoice_none() throws Exception {
        String uri = "http://localhost:" + port + "/invoice/find?storeNumber={storeNum}&trailerNumber={trailerNum}";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

        String storeNum = "NONE";
        String trailerNum = "YT-1300";

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, String.class, storeNum, trailerNum);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

}
