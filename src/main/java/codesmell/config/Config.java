package codesmell.config;

import codesmell.foo.FooBar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public FooBar doFooBar() {
        return new FooBar();
    }
    
}
