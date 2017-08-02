package codesmell.invoice.rest.spring;

import codesmell.foo.FooBar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloSpringController {

    @Autowired
    FooBar foobar;

    @RequestMapping(
            path = "/foo",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String fooBar() {
        return "Hello " + foobar.getHello() + " from Spring MVC";
    }
}
