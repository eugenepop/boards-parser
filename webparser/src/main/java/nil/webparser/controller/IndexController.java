package nil.webparser.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nil.webparser.dao.AdvertRepository;
import nil.webparser.dto.ImageUnit;
import nil.webparser.entity.Advert;
import nil.webparser.entity.AdvertImage;
import nil.webparser.service.CollageMaker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Controller
public class IndexController {

    private final AdvertRepository advertRepository;
    private final CollageMaker collageMaker;

    @GetMapping(value = "/")
    private String index(Principal principal, Model model) {
        log.info(principal != null ? principal.toString() : "principal == null");

        Page<Advert> page = advertRepository.findWithPagination(PageRequest.of(0, 30));
        List<Advert> adverts = page.toList();

        for (Advert advert : adverts) {
            List<AdvertImage> images = advert.getImages();

            List<ImageUnit> imageUnits = images.stream()
                    .map(it -> new ImageUnit(it.getId(), it.getWidth(), it.getHeight(), it.getFileName()))
                    .collect(Collectors.toList());

            List<ImageUnit> transformedImages = collageMaker.transform(imageUnits);
            advert.setTransformedImages(transformedImages);
        }

        model.addAttribute("adverts", adverts);

        return "index";
    }
}
