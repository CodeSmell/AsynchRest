package codesmell.invoice.rest.spring.sync;

import codesmell.invoice.config.Config;
import codesmell.invoice.config.DaoConfig;
import codesmell.invoice.dao.InvoiceDao;
import codesmell.invoice.dao.InvoiceDaoException;
import codesmell.invoice.dao.InvoiceMetaData;
import codesmell.invoice.rest.spring.GlobalExceptionHandlerControllerAdvice;
import codesmell.invoice.rest.spring.sync.InvoiceController;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { InvoiceController.class, GlobalExceptionHandlerControllerAdvice.class })
@EnableWebMvc
@AutoConfigureMockMvc
@ContextConfiguration(classes = { Config.class, DaoConfig.class })
public class InvoiceControllerMockMvcTest {

	private static final String BASE_URL = "/springmvc/invoice/find";
	
    @Autowired
    private MockMvc mockMvc;

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

        this.mockMvc.perform(get(BASE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .param("storeNumber", "100")
                .param("trailerNumber", "YT-1300"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()", Matchers.is(3)))
            .andExpect(jsonPath("$[0].packId", Matchers.notNullValue()))
            .andExpect(jsonPath("$[2].packId", Matchers.notNullValue()));
    }

    @Test
    public void test_invoice_none_shipment() throws Exception {

        when(mockDao.findInvoiceByDestination(any(), any())).thenReturn(new ArrayList<InvoiceMetaData>());

        this.mockMvc.perform(get(BASE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .param("storeNumber", "100")
                .param("trailerNumber", "YT-1300"))
            .andExpect(status().isNoContent())
            .andExpect(content().string(""));
    }

    @Test
    public void test_invoice_dao_exception() throws Exception {

        when(mockDao.findInvoiceByDestination(any(), any())).thenThrow(new InvoiceDaoException());

        this.mockMvc.perform(get(BASE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .param("storeNumber", "100")
                .param("trailerNumber", "YT-1300"))
            .andExpect(status().isInternalServerError())
            .andExpect(content().string(""));
    }

}
