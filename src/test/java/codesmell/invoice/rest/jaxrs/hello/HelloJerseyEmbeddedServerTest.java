package codesmell.invoice.rest.jaxrs.hello;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class HelloJerseyEmbeddedServerTest {

    @LocalServerPort
    int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void test_jersey_hello_jaxrsClient() {
        Client jaxRsClient = ClientBuilder.newClient();
        WebTarget webTarget = jaxRsClient.target("http://localhost:" + port + "/jaxrs");
        WebTarget helloTarget = webTarget.path("hello");

        Response response = helloTarget.request()
            .accept(MediaType.TEXT_PLAIN_TYPE)
            .get();

        assertEquals(200, response.getStatus());
        assertEquals("Hello world from JAX-RS (Jersey)", response.readEntity(String.class));
    }

    @Test
    public void test_jersey_hello_springRestTemplate() throws Exception {
        String uri = "http://localhost:" + port + "/jaxrs/hello";
        String responseText = restTemplate.getForObject(uri, String.class);
        assertNotNull(responseText);
        assertEquals("Hello world from JAX-RS (Jersey)", responseText);
    }
}
