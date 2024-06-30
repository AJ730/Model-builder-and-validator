package nl.tudelft.sp.modelchecker.parsers.CsvParser;

import java.util.ArrayList;
import java.util.List;
import lombok.NoArgsConstructor;
import nl.tudelft.sp.modelchecker.entities.Record;
import nl.tudelft.sp.modelchecker.parsers.CsvParser.config.RecordConvertor;
import org.springframework.web.multipart.MultipartFile;


@NoArgsConstructor
public abstract class AbstractAdaptor implements RecordConvertor<List<Record>> {

    MultipartFile file;
    List<Record> recordList;

    /**
     * Initialize a AbstractAdaptor.
     *
     * @param file file
     */
    public AbstractAdaptor(MultipartFile file) {
        if (!hasCsvformat(file)) throw new AssertionError();

        this.file = file;
        this.recordList = new ArrayList<>();
    }

    /**
     * Function to check if it has a csvFormat.
     *
     * @param file file
     * @return boolean
     */
    public final boolean hasCsvformat(MultipartFile file) {
        return TYPE.equals(file.getContentType())
                || TYPEMS.equals(file.getContentType())
                || EXCEL.equals(file.getContentType());
    }

}
