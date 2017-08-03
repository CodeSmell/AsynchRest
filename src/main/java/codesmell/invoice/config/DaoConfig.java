package codesmell.invoice.config;

import codesmell.invoice.dao.InvoiceDao;
import codesmell.invoice.dao.impl.SlowInvoiceDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DaoConfig {

    @Bean
    public InvoiceDao doDao() {
        return new SlowInvoiceDao();
    }

}
