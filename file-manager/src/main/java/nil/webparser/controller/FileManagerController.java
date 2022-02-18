//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.util.StreamUtils;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.io.IOException;
//
//@Slf4j
//@RequiredArgsConstructor
//@RestController
//@RequestMapping("/image")
//public class FileManagerController {
//
//    @Value("${upload.image.directory}")
//    private String IMAGE_DESTINATION_FOLDER;
//
//    private final AdvertImageRepository advertImageRepository;
//
//    @GetMapping(value = "/{fileName}", produces = MediaType.IMAGE_JPEG_VALUE)
//    private ResponseEntity<byte[]> getImage(@PathVariable("fileName") String fileName) throws IOException {
//        ClassPathResource imageFileStream = new ClassPathResource(IMAGE_DESTINATION_FOLDER + fileName);
//        byte[] bytes = StreamUtils.copyToByteArray(imageFileStream.getInputStream());
//        return ResponseEntity
//                .ok()
//                .contentType(MediaType.IMAGE_JPEG)
//                .body(bytes);
//    }
//}
