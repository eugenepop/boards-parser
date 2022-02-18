package nil.webparser.service;

import lombok.extern.slf4j.Slf4j;
import nil.webparser.entity.AdvertImage;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;

import static java.time.format.DateTimeFormatter.ofPattern;
import static org.apache.commons.lang3.RandomStringUtils.random;

@Slf4j
@Component
public class ImageFileManager {

    @Value("${upload.images.directory}")
    private String IMAGE_DESTINATION_FOLDER;

    public AdvertImage processOne(AdvertImage image) throws IOException {

        String imageUrl = image.getSourceLink();

        String extension = imageUrl.substring(imageUrl.lastIndexOf("."));

        if (StringUtils.isEmpty(extension)) {
            log.error("extension cannot be defined, image url = {}", imageUrl);
            image.setProcessed(true);
            image.setSuccess(false);
            return image;
        }

        String imageFileName = ofPattern("yyyyMMddHHmmssSSS").format(LocalDateTime.now()) + random(8, false, true) + extension;

        BufferedImage bufferedImage;
        boolean highQualityFlag = true;
        try {
            bufferedImage = ImageIO.read(new URL(imageUrl.replaceFirst("208x156", "1280x960")));
        } catch (Exception e) {
            highQualityFlag = false;
            log.error(e.getMessage());
            try {
                bufferedImage = ImageIO.read(new URL(imageUrl.replaceFirst("208x156", "640x480")));
            } catch (IIOException ex) {
                log.error(ex.getMessage());
                image.setProcessed(true);
                image.setSuccess(false);
                return image;
            }
        }

        int height = bufferedImage.getHeight();
        int width = bufferedImage.getWidth();
        float k = (float) height / width;
        int cropLogo = (highQualityFlag) ? 50 : 40;
        BufferedImage croppedImage = bufferedImage.getSubimage(0, 0, Math.round((float) (height - cropLogo) / k), height - cropLogo);

        File imageFile = new File(IMAGE_DESTINATION_FOLDER + imageFileName);
        ImageIO.write(croppedImage, FilenameUtils.getExtension(imageFile.getName()), imageFile);

        image.setFileName(imageFileName);
        image.setWidth(width);
        image.setHeight(height);
        image.setProcessed(true);
        image.setSuccess(true);

        return image;
    }
}
