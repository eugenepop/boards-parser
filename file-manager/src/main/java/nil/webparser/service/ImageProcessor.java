package nil.webparser.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nil.webparser.dao.AdvertImageRepository;
import nil.webparser.entity.AdvertImage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Component
public class ImageProcessor {

    @Value("${parser.images.timeout.seconds}")
    private long parserTimeout;

    private final AdvertImageRepository advertImageRepository;
    private final ImageFileManager imageFileManager;

    public void process() {

        List<AdvertImage> imagesToProcess = advertImageRepository.findAllByProcessedIsFalse();

        for (AdvertImage image : imagesToProcess) {

            try {
                AdvertImage processedImage = imageFileManager.processOne(image);
                advertImageRepository.save(processedImage);
                log.info("image processing, id = {}", image.getId());
                TimeUnit.SECONDS.sleep(parserTimeout);
            } catch (Exception ex) {
                log.error("error occurred while image processing, id = {}", image.getId(), ex);
            }

        }

    }
}
