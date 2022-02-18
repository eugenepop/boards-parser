package nil.webparser.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import nil.webparser.service.AdvertService;
import nil.webparser.entity.Advert;
import nil.webparser.parser.AdvertParserManager;
import nil.webparser.service.SpreadSheetManager;

import java.util.List;

@RestController
@RequestMapping("/admin/adverts")
@Slf4j
public class ParserController {

    private final AdvertParserManager parserManager;
    private final AdvertService advertService;
    private final SpreadSheetManager spreadSheetManager;

    public ParserController(AdvertParserManager parserManager,
                            AdvertService advertService,
                            SpreadSheetManager spreadSheetManager) {
        this.parserManager = parserManager;
        this.advertService = advertService;
        this.spreadSheetManager = spreadSheetManager;
    }

    @GetMapping(value = "",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE)
    private List<Advert> getAvitoAdverts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return advertService.getAdverts(page, size);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/export",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE)
    private void exportAvitoAdverts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        List<Advert> adverts = advertService.getAdverts(page, size);
        spreadSheetManager.generate(adverts);
    }
}
