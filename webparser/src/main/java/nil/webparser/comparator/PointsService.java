package nil.webparser.comparator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PointsService {

    @Value(value = "classpath:data/AE_Points_08242020072406.txt")
    private Resource brierlyPointsFile;

    @Value(value = "classpath:data/AE_Points_08242020.txt")
    private Resource loyaltyPointsFile;

    private final PointsParser pointsParser;
    private final PointsBrierlyParser pointsBrierlyParser;
    private final PointsBrierlyRepository pointsBrierlyRepository;
    private final PointsRepository pointsRepository;

    //@PostConstruct
    private void parseAndSaveAllBrierlyPoints() throws IOException {

        List<PointsBrierlyRow> points;

        try (FileInputStream input = new FileInputStream(brierlyPointsFile.getFile())) {
            points = pointsBrierlyParser.parse(input);
        }

        pointsBrierlyRepository.saveAll(points);
        log.info("all the brierly-related points were successfully saved");
    }

    //@PostConstruct
    private void parseAndSaveAllLoyaltyPoints() throws IOException {

        List<PointsRow> points;

        try (FileInputStream input = new FileInputStream(loyaltyPointsFile.getFile())) {
            points = pointsParser.parse(input);
        }

        pointsRepository.saveAll(points);
        log.info("all the loyalty-related points were successfully saved");
    }
}
