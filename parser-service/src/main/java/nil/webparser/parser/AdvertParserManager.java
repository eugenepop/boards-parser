package nil.webparser.parser;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nil.webparser.service.AdvertService;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import nil.webparser.config.ParserConfiguration;
import nil.webparser.entity.Advert;
import nil.webparser.entity.AdvertImage;
import nil.webparser.entity.AdvertProperty;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Slf4j
@RequiredArgsConstructor
@Component
public class AdvertParserManager {

    private final static String STUDIO = "Студия";
    private final static String APARTMENT = "Квартира";
    private final static String CATEGORY = "Категория";
    private final static String TYPE = "Тип объявления";
    private final static String HOUSE_TYPE = "Тип дома";
    private final static String TENANCY = "Срок аренды";
    private final static String KITCHEN_SQUARE = "Площадь кухни";
    private final static String LIVING_SQUARE = "Жилая площадь";
    private final static String PLEDGE = "Залог";
    private final static String COMMISSION = "Размер комиссии";
    private final static String OWNERSHIP = "Право собственности";
    private final static String ROOM_TYPE = "roomType";
    private final static String ROOM_COUNT = "roomCount";
    private final static String WHOLE_SQUARE = "wholeSquare";
    private final static String FLOOR = "floor";
    private final static String OF_FLOOR = "ofFloor";
    private final static List<String> DISTRICTS = Arrays.asList(
            "Волжский",
            "Заводской",
            "Ленинский",
            "Фрунзенский",
            "Октябрьский",
            "Кировский");

    @Value("{$parser.userAgent.ios}")
    private String IOS_USER_AGENT;
    @Value("${parser.userAgent.desktop}")
    private String DESKTOP_USER_AGENT;
    @Value("${parser.adverts.timeout.seconds}")
    private long parserTimeout;
    @Value("${parser.pageCount}")
    private long pageCount;

    private final AdvertService advertService;
    private final ParserConfiguration parserConfiguration;

    public void parse() throws IOException {

        List<Advert> adverts = extractAdvertInitInfo(pageCount);

        for (Advert advert : adverts) {
            String link = advert.getLink();

            if (advertService.existByLink(link)) {
                log.info("skip since adv already exists = {}", link);
                continue;
            }

            System.setProperty("chromeoptions.args", "--user-agent=" + IOS_USER_AGENT);

            String mobileVersionLink = link.replaceFirst("https://www", "https://m");
            Selenide.open(mobileVersionLink);

            $(parserConfiguration.getShowPhoneButtonSelector()).waitUntil(Condition.visible, 10000).click();
            SelenideElement sellerPhoneElement = $(parserConfiguration.getPhoneNumberSelector());

            try {
                TimeUnit.MILLISECONDS.sleep(RandomUtils.nextLong(1000 * (parserTimeout - 3), 1000 * (parserTimeout + 3)));
            } catch (InterruptedException e) {
                log.info(e.getMessage());
            }

            SelenideElement title = $(parserConfiguration.getTitleSelector());
            SelenideElement price = $(parserConfiguration.getPriceSelector());
            SelenideElement description = $(parserConfiguration.getDescriptionSelector());
            SelenideElement sellerName = $(parserConfiguration.getSellerNameSelector());

            ElementsCollection properties = $$(parserConfiguration.getPropertiesSelector());
            List<AdvertProperty> advertPropertyList = extractProps(properties, title);

            try {
                advert.setTitle(title.innerText());
                advert.setLink(link);
                advert.setDescription(description.attr("content"));
                advert.setSellerName(sellerName.innerText());
                advert.setSellerPhone(sellerPhoneElement.innerText().replaceAll("tel:\\+", ""));
                advert.setPrice(Long.valueOf(price.innerText().replaceAll("\\D+", "")));
                advert.setProperties(advertPropertyList);
            } catch (NullPointerException npe) {
                log.warn(link);
                continue;
            }

            advertService.save(advert);

            log.info(advert.toString());
        }
    }

    private List<Advert> extractAdvertInitInfo(long pageCount) throws IOException {
        List<Advert> adverts = new ArrayList<>();

        for (int i = 1; i <= pageCount; i++) {
            Document doc;
            try {
                doc = Jsoup.connect(String.format("https://avito.ru/saratov/kvartiry/sdam?p=%d", i))
                        .userAgent(DESKTOP_USER_AGENT)
                        .referrer("http://www.google.com")
                        .timeout(5000)
                        .get();
            } catch (HttpStatusException | SocketTimeoutException e) {
                log.info("error occurred during getting set of advert links: ", e);
                continue;
            }

            if (doc.location().contains("blocked")) {
                log.info("blocked %)");
                continue;
            }

            Elements elements = doc.select(".snippet-horizontal");
            elements.forEach(it -> {
                Advert advert = new Advert();

                Element linkElement = it.selectFirst(parserConfiguration.getLinksSelector());
                advert.setLink(linkElement.attr("abs:href"));

                Element districtElement = it.selectFirst(".item-address-georeferences");
                String district = districtElement.text();
                advert.setDistrict(extractDistrict(district));

                Element addressElement = it.selectFirst(".item-address__string");
                advert.setAddress(addressElement.text());

                //get links of images to be downloaded further
                Elements imageElements = it.select(".item-slider-item div img");
                Set<String> imageLinkSet = new HashSet<>();
                imageElements.forEach(image -> {
                    //https://58.img.avito.st/208x156/9053183358.jpg 1x, https://58.img.avito.st/image/1/o1-pT7a_D7bf6v2w4xnpDVzsCbwXLAtEG-wNsBHqDbYdqg 1.5x
                    String attr_datasrcset = image.attr("data-srcset");
                    String attr_srcset = image.attr("srcset");

                    if (isEmpty(attr_datasrcset) && isEmpty(attr_srcset)) {
                        log.error("cant find image link in attr (srcset/data-srcset)");
                    }

                    String imageLink;
                    if (StringUtils.isNotEmpty(attr_datasrcset)) {
                        imageLink = StringUtils.substringBefore(attr_datasrcset, " 1x,");
                    } else {
                        imageLink = StringUtils.substringBefore(attr_srcset, " 1x,");
                    }
                    imageLinkSet.add(imageLink);
                });

                advert.setImages(imageLinkSet.stream()
                        .map(AdvertImage::new)
                        .collect(Collectors.toList()));

                adverts.add(advert);
            });
        }

        return adverts;
    }

    private String extractDistrict(String districtElement) {
        for (String district : DISTRICTS) {
            if (districtElement.contains(district)) {
                return district;
            }
        }
        return EMPTY;
    }

    private List<AdvertProperty> extractProps(ElementsCollection properties, SelenideElement title) {
        List<AdvertProperty> advertPropertyList = new ArrayList<>();

        for (int p = 0; p < properties.size(); p++) {
            String propNameText = properties.get(p).innerText();
            String propValue = null;
            AdvertProperty.Properties propName = null;
            switch (propNameText) {
                case CATEGORY:
                    propValue = properties.get(p + 1).innerText();
                    propName = AdvertProperty.Properties.CATEGORY;
                    break;
                case TYPE:
                    propValue = properties.get(p + 1).innerText();
                    propName = AdvertProperty.Properties.TYPE;
                    break;
                case HOUSE_TYPE:
                    propValue = properties.get(p + 1).innerText();
                    propName = AdvertProperty.Properties.HOUSE_TYPE;
                    break;
                case TENANCY:
                    propValue = properties.get(p + 1).innerText();
                    propName = AdvertProperty.Properties.TENANCY;
                    break;
                case KITCHEN_SQUARE:
                    propValue = properties.get(p + 1).innerText();
                    propName = AdvertProperty.Properties.KITCHEN_SQUARE;
                    break;
                case LIVING_SQUARE:
                    propValue = properties.get(p + 1).innerText();
                    propName = AdvertProperty.Properties.LIVING_SQUARE;
                    break;
                case PLEDGE:
                    propValue = properties.get(p + 1).innerText();
                    propName = AdvertProperty.Properties.PLEDGE;
                    break;
                case OWNERSHIP:
                    propValue = properties.get(p + 1).innerText();
                    propName = AdvertProperty.Properties.OWNERSHIP;
                    break;
                case COMMISSION:
                    propValue = properties.get(p + 1).innerText();
                    propName = AdvertProperty.Properties.COMMISSION;
                    break;
            }
            if (Objects.nonNull(propName) && Objects.nonNull(propValue)) {
                advertPropertyList.add(AdvertProperty.builder()
                        .name(propName)
                        .value(propValue)
                        .build());
            }
        }

        List<String> groupNames = Arrays.asList(ROOM_TYPE, ROOM_COUNT, WHOLE_SQUARE, FLOOR, OF_FLOOR);
        Matcher matcher = Pattern.compile("^(?<roomType>(?<roomCount>\\d)-к квартира|Студия),\\s" +
                "(?<wholeSquare>\\d+).*(?<floor>\\d+)/(?<ofFloor>\\d+).*$").matcher(title.text());
        if (matcher.find()) {
            for (String groupName : groupNames) {
                String propValue = null;
                AdvertProperty.Properties propName = null;
                switch (groupName) {
                    case ROOM_TYPE:
                        propValue = matcher.group(ROOM_TYPE).equals(STUDIO) ? STUDIO : APARTMENT;
                        propName = AdvertProperty.Properties.ROOM_TYPE;
                        break;
                    case ROOM_COUNT:
                        propValue = matcher.group(ROOM_TYPE).equals(STUDIO) ? "1" : matcher.group(groupName);
                        propName = AdvertProperty.Properties.ROOM_COUNT;
                        break;
                    case WHOLE_SQUARE:
                        propValue = matcher.group(groupName);
                        propName = AdvertProperty.Properties.WHOLE_SQUARE;
                        break;
                    case FLOOR:
                        propValue = matcher.group(groupName);
                        propName = AdvertProperty.Properties.FLOOR;
                        break;
                    case OF_FLOOR:
                        propValue = matcher.group(groupName);
                        propName = AdvertProperty.Properties.OF_FLOOR;
                        break;
                }
                if (Objects.nonNull(propName) && Objects.nonNull(propValue)) {
                    advertPropertyList.add(AdvertProperty.builder()
                            .name(propName)
                            .value(propValue)
                            .build());
                }
            }
        }

        return advertPropertyList;
    }
}
