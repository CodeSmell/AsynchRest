package codesmell.invoice.config;

import codesmell.foo.FooBar;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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

    @Bean
    public ObjectMapper doObjectMapper() {
        return new ObjectMapper()
            .configure(SerializationFeature.WRAP_ROOT_VALUE, false)
            .configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .configure(SerializationFeature.INDENT_OUTPUT, false);
    }

}
