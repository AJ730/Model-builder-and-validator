package nl.tudelft.sp.modelchecker.decoders;

import java.io.*;
import javax.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;


public class Base64DecodedMultipartFile implements MultipartFile {

    private final byte[] imgContent;
    private final String name;

    /**
     * Constructor for base64DecodedMultipartFile.
     *
     * @param imgContent imgContent of file
     * @param name       name of file
     */
    public Base64DecodedMultipartFile(byte[] imgContent, String name) {
        this.imgContent = imgContent;
        this.name = name;
    }

    /**
     * Get name of MultipartFile.
     *
     * @return name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Get original filename of Multipartfile.
     *
     * @return original filename
     */
    @Override
    public String getOriginalFilename() {
        return name;
    }

    /**
     * Get contentType of Multipart file.
     *
     * @return content type
     */
    @Override
    public String getContentType() {
        return "mp4";
    }

    /**
     * file Is empty.
     *
     * @return if file is empty
     */
    @Override
    public boolean isEmpty() {
        return imgContent.length == 0;
    }

    /**
     * Get size of file.
     *
     * @return size
     */
    @Override
    public long getSize() {
        return imgContent.length;
    }

    /**
     * Get bytes of file.
     *
     * @return bytes
     */
    @Override
    public byte[] getBytes() {
        return imgContent;
    }

    /**
     * Return inputStream of file.
     *
     * @return inputStream
     */
    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(imgContent);
    }

    /**
     * Transfer the file to a location.
     *
     * @param dest dest
     * @throws IOException           IOException
     * @throws IllegalStateException IllegalStateException
     */
    @Override
    public void transferTo(@NotNull File dest) throws IOException, IllegalStateException {
        new FileOutputStream(dest).write(imgContent);
    }
}
