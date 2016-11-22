package codesmell.foo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FooBar {
    private static final Logger LOGGER = LoggerFactory.getLogger(FooBar.class);
    
    public FooBar() {
        LOGGER.debug("foo bar!");
    }
}
