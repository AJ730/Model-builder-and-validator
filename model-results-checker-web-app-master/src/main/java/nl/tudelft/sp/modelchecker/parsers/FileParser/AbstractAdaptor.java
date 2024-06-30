package nl.tudelft.sp.modelchecker.parsers.FileParser;

import java.util.ArrayList;
import java.util.List;
import lombok.NoArgsConstructor;
import nl.tudelft.sp.modelchecker.parsers.FileParser.config.TextConvertor;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
public abstract class AbstractAdaptor implements TextConvertor<List<String>> {

    MultipartFile file;
    List<String> classList;

    /**
     * Initialize a AbstractAdaptor.
     *
     * @param file file
     */
    public AbstractAdaptor(MultipartFile file) {
        if (!hasTextFormat(file)) throw new AssertionError();

        this.file = file;
        this.classList = new ArrayList<>();
    }

    /**
     * Function to check if it has a csvFormat.
     *
     * @param file file
     * @return boolean
     */
    public final boolean hasTextFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }

}
