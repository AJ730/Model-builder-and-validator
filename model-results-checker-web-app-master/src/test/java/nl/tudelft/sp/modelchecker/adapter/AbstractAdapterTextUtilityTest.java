package nl.tudelft.sp.modelchecker.adapter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import nl.tudelft.sp.modelchecker.Application;
import nl.tudelft.sp.modelchecker.parsers.FileParser.Adapter;
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
public class AbstractAdapterTextUtilityTest {


    MultipartFile multipartFile;

    Adapter adapter;

    /**
     * Set Up test environment.
     */
    @BeforeEach
    public void setUp() throws IOException {

        // arrange
        File file = new File("src/test/resources/classes.txt");
        FileInputStream input = new FileInputStream(file);

        multipartFile = new MockMultipartFile(
                "classes", file.getName(),
                "text/plain", input.readAllBytes());

        input.close();

        adapter = new Adapter(multipartFile);
    }

    /**
     * Attempt to store text.
     */
    @Test
    public void attemptToGetClassesFromTxtFile() throws IOException {

        // arrange
        List<String> classes = adapter.getClasses(multipartFile);

        // act and assert
        assertThat(classes.size()).isEqualTo(13);

        // arrange
        List<String> result = Arrays.asList("aluminium", "film", "hdpe_jazz",
                "hdpe_nat", "paper", "pet_blue", "pet_jazz",
                "pet_nat", "pet_tray", "pp", "residual",
                "steel", "wrapped_pet");

        // act and assert
        assertThat(result).isEqualToComparingOnlyGivenFields(classes);
    }


    /**
     * Check if the format is correct.
     */
    @Test
    public void assertionErrorNoCorrectFormat() {

        // act and assert
        assertThrows(AssertionError.class, () -> {
            adapter = new Adapter(new MockMultipartFile("csv", new byte[]{}));
        });

    }

}
