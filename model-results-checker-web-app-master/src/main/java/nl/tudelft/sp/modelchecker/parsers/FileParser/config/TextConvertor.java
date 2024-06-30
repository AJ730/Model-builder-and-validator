package nl.tudelft.sp.modelchecker.parsers.FileParser.config;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface TextConvertor<E> extends Configuration {

    /**
     * Get Classes from a multipartFile.
     *
     * @param multipartFile multipartFile
     * @return E
     * @throws IOException IOException
     */
    E getClasses(MultipartFile multipartFile) throws IOException;


}
