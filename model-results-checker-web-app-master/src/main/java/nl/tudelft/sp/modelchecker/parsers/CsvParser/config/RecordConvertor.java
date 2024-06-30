package nl.tudelft.sp.modelchecker.parsers.CsvParser.config;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface RecordConvertor<E> extends Configuration {

    /**
     * Get records from a multipartFile.
     *
     * @param multipartFile multipartFile
     * @return E
     * @throws IOException IOException
     */
    E getRecords(MultipartFile multipartFile) throws IOException;


}
