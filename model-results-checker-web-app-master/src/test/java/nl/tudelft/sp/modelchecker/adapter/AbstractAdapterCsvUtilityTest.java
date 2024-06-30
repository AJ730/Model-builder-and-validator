package nl.tudelft.sp.modelchecker.adapter;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import nl.tudelft.sp.modelchecker.Application;
import nl.tudelft.sp.modelchecker.parsers.CsvParser.Adapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Transactional
public class AbstractAdapterCsvUtilityTest {

    MultipartFile multipartFile;

    byte[] bytes;

    Adapter adapter;

    String extension = "application/octet-stream";

    /**
     * Set Up test environment.
     */
    @BeforeEach
    public void setUp() {

        // arrange
        multipartFile = new MockMultipartFile(extension, "test", extension, bytes);
        adapter = new Adapter(multipartFile);
    }

    /**
     * Check if the format is correct.
     */
    @Test
    public void hasCsvFormatTest() {

        // act and assert
        assertThat(adapter.hasCsvformat(multipartFile)).isTrue();
    }


    /**
     * Check if the format is correct.
     */
    @Test
    public void hasSecondaryCsvFormatTest() {

        // arrange
        multipartFile = new MockMultipartFile(extension, "test",
                "text/csv", bytes);

        // act and assert
        assertThat(adapter.hasCsvformat(multipartFile)).isTrue();
    }

    /**
     * Check if the format is correct.
     */
    @Test
    public void hasThirdCsvFormatTest() {

        // arrange
        multipartFile = new MockMultipartFile(extension, "test",
                "application/vnd.ms-excel", bytes);

        // act and assert
        assertThat(adapter.hasCsvformat(multipartFile)).isTrue();
    }


    /**
     * Check if the format is correct.
     */
    @Test
    public void assertionErrorNoCorrectFormat() {

        // act and assert
        assertThrows(AssertionError.class, () -> {
            adapter = new Adapter(new MockMultipartFile(extension, bytes));
        });

    }

}
