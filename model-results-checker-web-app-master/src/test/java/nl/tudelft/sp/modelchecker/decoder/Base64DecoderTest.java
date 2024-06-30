package nl.tudelft.sp.modelchecker.decoder;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import nl.tudelft.sp.modelchecker.Application;
import nl.tudelft.sp.modelchecker.decoders.Base64DecodedMultipartFile;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class Base64DecoderTest {

    private MultipartFile multipartFile;
    private String name;

    /**
     * Set environment for test.
     */
    @BeforeAll
    public void setUp() {

        // arrange
        this.name = "test";
        this.multipartFile = new Base64DecodedMultipartFile(new byte[]{12, 23}, name);

    }

    /**
     * Get name test.
     */
    @Test
    public void getNameTest() {

        // act and assert
        assertThat(multipartFile.getName()).isEqualTo(name);
    }

    /**
     * Get original file test.
     */
    @Test
    public void getOriginalFilenameTest() {

        // act and assert
        assertThat(multipartFile.getOriginalFilename()).isEqualTo(name);
    }

    /**
     * Get content type test.
     */
    @Test
    public void getContentTypeTest() {

        // act and assert
        assertThat(multipartFile.getContentType()).isEqualTo("mp4");
    }

    /**
     * Is empty test.
     */
    @Test
    public void isEmptyTestImageContentTest() {

        // arrange
        multipartFile = new Base64DecodedMultipartFile(new byte[]{}, name);

        // act and assert
        assertThat(multipartFile.isEmpty()).isTrue();
    }

    /**
     * Is not empty test image.
     */
    @Test
    public void isNotEmptyTestImageContentTest() {

        // act and assert
        assertThat(multipartFile.isEmpty()).isFalse();
    }

    /**
     * Get size test.
     */
    @Test
    public void getSizeTest() {

        // act and assert
        assertThat(multipartFile.getSize()).isEqualTo(2);
    }

    /**
     * Get bytes test.
     *
     * @throws IOException IOException
     */
    @Test
    public void getBytesTest() throws IOException {

        // act and assert
        assertThat(multipartFile.getBytes()).isEqualTo(new Byte[]{12, 23});
    }

    /**
     * Get input stream test.
     *
     * @throws IOException IOException
     */
    @Test
    public void getInputStreamTest() throws IOException {

        // act and assert
        assertThat(multipartFile.getInputStream()).isInstanceOf(ByteArrayInputStream.class);
    }

    /**
     * Transfer to a file test.
     *
     * @throws IOException IOException
     */
    @Test
    public void transferToTest() throws IOException {

        // arrange
        File file = new File("src/test/resources/test.txt");

        // act
        multipartFile.transferTo(file);

        // assert
        assertThat(file.exists()).isTrue();
        assertThat(file.getName()).isEqualTo("test.txt");
    }
}
