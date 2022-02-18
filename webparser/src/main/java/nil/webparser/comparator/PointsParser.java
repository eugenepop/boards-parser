package nil.webparser.comparator;

import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
public class PointsParser implements FileParser<PointsRow> {

    private static final char DEFAULT_DELIMITER = '|';
    private static final String DEFAULT_LINE_SEPARATOR = "\n";

    @Override
    public List<PointsRow> parse(InputStream inputStream) {
        CsvParserSettings settings = new CsvParserSettings();
        settings.setAutoConfigurationEnabled(true);
        settings.setHeaderExtractionEnabled(false);
        settings.getFormat().setLineSeparator(DEFAULT_LINE_SEPARATOR);
        settings.getFormat().setDelimiter(DEFAULT_DELIMITER);

        //settings.setMaxCharsPerColumn(100_000);

        BeanListProcessor<PointsRow> processor = new BeanListProcessor<>(PointsRow.class);
        settings.setProcessor(processor);

        CsvParser parser = new CsvParser(settings);

        parser.parse(inputStream);

        return processor.getBeans();
    }
}
