package nil.webparser.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import nil.webparser.service.ImageProcessor;

@RequiredArgsConstructor
@Component
public class FileManagerScheduler {

    private final ImageProcessor imageParserManager;

    @Scheduled(fixedDelayString = "${parser.images.fixedDelay.milliseconds}", initialDelayString = "${parser.images.initialDelay.milliseconds}")
    public void processImages() {
        imageParserManager.process();
    }

}
