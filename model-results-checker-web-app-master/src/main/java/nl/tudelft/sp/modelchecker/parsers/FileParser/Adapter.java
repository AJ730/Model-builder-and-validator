package nl.tudelft.sp.modelchecker.parsers.FileParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;
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

    private List<String> storeText(InputStream inputStream) {

        Scanner sc = new Scanner(inputStream);
        while (sc.hasNextLine()) {
            classList.add(sc.nextLine());
        }
        sc.close();
        return classList;
    }


    @Override
    public List<String> getClasses(MultipartFile multipartFile) throws IOException {
        return storeText(multipartFile.getInputStream());
    }
}
