package nil.webparser.parser;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class ParseScheduler {

    private final AdvertParserManager advertParserManager;

    @Scheduled(fixedDelayString = "${parser.adverts.fixedDelay.milliseconds}", initialDelayString = "${parser.adverts.initialDelay.milliseconds}")
    private void parseAdverts() throws IOException {
        advertParserManager.parse();
    }
}
