package codesmell.invoice.config;

import codesmell.foo.FooBar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource(value = { "classpath:config.props" })
public class Config {

    /**
     * used by Spring to resolve @Value configuration
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfig() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public FooBar doFooBar() {
        return new FooBar();
    }

}
