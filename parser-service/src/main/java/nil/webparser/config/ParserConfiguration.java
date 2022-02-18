package nil.webparser.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "parser.avito")
public class ParserConfiguration {

    private String linksSelector;
    private String showPhoneButtonSelector;
    private String phoneNumberSelector;
    private String titleSelector;
    private String priceSelector;
    private String addressSelector;
    private String descriptionSelector;
    private String sellerNameSelector;
    private String propertiesSelector;
    private String citySelector;
    private String districtSelector;
    private String imagesSelector;

}
