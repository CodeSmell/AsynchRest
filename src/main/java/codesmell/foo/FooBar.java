package codesmell.foo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

public class FooBar implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(FooBar.class);

    @Value("${hello}")
    private String helloValue;

    public FooBar() {
        LOGGER.debug("foo bar!");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.debug("the value of the property hello is:" + helloValue);
    }

    public String getHello() {
        return helloValue;
    }
}
