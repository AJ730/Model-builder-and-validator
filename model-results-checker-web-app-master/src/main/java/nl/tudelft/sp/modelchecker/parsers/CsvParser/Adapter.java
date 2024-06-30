package nl.tudelft.sp.modelchecker.parsers.CsvParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import nl.tudelft.sp.modelchecker.entities.Record;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

public class Adapter extends AbstractAdaptor {

    /**
     * Initialize an adapter.
     *
     * @param file file
     */
    public Adapter(MultipartFile file) {
        super(file);
    }

    /**
     * Store Csv.
     *
     * @param stream stream
     * @return List of records
     * @throws IOException IOException
     */
    @SuppressWarnings("PMD.CloseResource")
    //PMD wants a try finally clause, but this is sufficient.
    private List<Record> storeCsv(InputStream stream) throws IOException {

        BufferedReader fileReader = new BufferedReader(new InputStreamReader(stream, global));
        CSVParser csvParser = new CSVParser(fileReader, csvFormat);

        Iterable<CSVRecord> csvRecordIterable = csvParser.getRecords();

        for (CSVRecord csvRecord : csvRecordIterable) {
            Record record = Record.builder()
                    .frameNum(Integer.parseInt(csvRecord.get("frame_num")))
                    .objectId(Integer.parseInt(csvRecord.get("object_id")))
                    .label((csvRecord.get("label")))
                    .trackerL(Integer.parseInt(csvRecord.get("tracker_l")))
                    .trackerT(Integer.parseInt(csvRecord.get("tracker_t")))
                    .trackerW(Integer.parseInt(csvRecord.get("tracker_w")))
                    .trackerH(Integer.parseInt(csvRecord.get("tracker_h")))
                    .modelConfidence(Double.parseDouble(csvRecord.get("model_confidence")))
                    .trackerConfidence(Double.parseDouble(csvRecord.get("tracker_confidence")))
                    .build();

            recordList.add(record);
        }

        fileReader.close();
        csvParser.close();
        return recordList;
    }

    /**
     * Get all the records from a multipartFile.
     *
     * @param multipartFile multipartFile
     * @return List of records
     * @throws IOException IOException
     */
    @Override
    public List<Record> getRecords(MultipartFile multipartFile) throws IOException {
        return storeCsv(multipartFile.getInputStream());
    }
}




