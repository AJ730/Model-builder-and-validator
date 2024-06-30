package nl.tudelft.sp.modelchecker.parsers.CsvParser.config;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.apache.commons.csv.CSVFormat;

public interface Configuration {

    String TYPE = "application/octet-stream";
    String TYPEMS = "text/csv";
    String EXCEL = "application/vnd.ms-excel";
    Charset global = StandardCharsets.UTF_8;
    CSVFormat csvFormat = CSVFormat.DEFAULT
            .withFirstRecordAsHeader()
            .withIgnoreHeaderCase()
            .withTrim();

}
