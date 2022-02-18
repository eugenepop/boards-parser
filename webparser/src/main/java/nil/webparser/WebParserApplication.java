package nil.webparser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class WebParserApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebParserApplication.class, args);
    }
}
