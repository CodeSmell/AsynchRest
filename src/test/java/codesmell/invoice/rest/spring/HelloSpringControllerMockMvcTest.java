package codesmell.invoice.rest.spring;

import codesmell.invoice.config.Config;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {HelloSpringController.class, GlobalExceptionHandlerControllerAdvice.class})
@EnableWebMvc
@AutoConfigureMockMvc
@ContextConfiguration(classes = Config.class)
public class HelloSpringControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void test_foobar_hello() throws Exception {
        this.mockMvc.perform(get("/foo"))
            .andExpect(status().isOk())
            .andExpect(content().string("Hello world from Spring MVC"));
    }

}
