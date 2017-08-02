package codesmell.invoice.rest.spring;

import codesmell.invoice.config.Config;
import codesmell.invoice.rest.spring.GlobalExceptionHandlerControllerAdvice;
import codesmell.invoice.rest.spring.InvoiceController;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {InvoiceController.class, GlobalExceptionHandlerControllerAdvice.class})
@EnableWebMvc
@AutoConfigureMockMvc
@ContextConfiguration(classes = Config.class)
public class InvoiceControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void test_foobar_hello() throws Exception {
        this.mockMvc.perform(get("/foo"))
            .andExpect(status().isOk())
            .andExpect(content().string("Hello world"));
    }

    @Test
    public void test_invoice() throws Exception {
        this.mockMvc.perform(get("/invoice/find")
                .accept(MediaType.APPLICATION_JSON)
                .param("storeNumber", "100")
                .param("trailerNumber", "YT-1300"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.length()", Matchers.is(1)))
            .andExpect(jsonPath("$[0].internalId", Matchers.notNullValue()))
            .andExpect(jsonPath("$[0].invoiceDestination.actorName", Matchers.is("100")))
            .andExpect(jsonPath("$[0].trailerNumber", Matchers.is("YT-1300")));
    }

    @Test
    public void test_invoice_none() throws Exception {
        this.mockMvc.perform(get("/invoice/find")
                .accept(MediaType.APPLICATION_JSON)
                .param("storeNumber", "NONE")
                .param("trailerNumber", "YT-1300"))
            .andExpect(status().isNoContent())
            .andExpect(content().string(""));
    }

}
