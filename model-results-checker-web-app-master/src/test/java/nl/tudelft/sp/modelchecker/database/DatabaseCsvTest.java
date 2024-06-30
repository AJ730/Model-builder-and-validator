package nl.tudelft.sp.modelchecker.database;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import javassist.NotFoundException;
import nl.tudelft.sp.modelchecker.Application;
import nl.tudelft.sp.modelchecker.dto.ContainerDto;
import nl.tudelft.sp.modelchecker.dto.CsvDto;
import nl.tudelft.sp.modelchecker.dto.RecordDto;
import nl.tudelft.sp.modelchecker.entities.Container;
import nl.tudelft.sp.modelchecker.entities.Csv;
import nl.tudelft.sp.modelchecker.entities.Record;
import nl.tudelft.sp.modelchecker.exceptions.ExistsException;
import nl.tudelft.sp.modelchecker.services.servicebeans.ContainerJpaService;
import nl.tudelft.sp.modelchecker.services.servicebeans.CsvJpaService;
import nl.tudelft.sp.modelchecker.services.servicebeans.RecordJpaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
public class DatabaseCsvTest {

    @Autowired
    CsvJpaService csvJpaService;

    @Autowired
    ContainerJpaService containerJpaService;

    @Autowired
    RecordJpaService recordJpaService;

    @Autowired
    ModelMapper modelMapper;

    private Csv csv;
    private Csv csv1;
    private CsvDto csvdto;

    private Container container;
    private Container container1;
    private ContainerDto containerDto;

    private Record record;
    private Record record2;

    private RecordDto recordDto;
    private RecordDto record2Dto;

    private MultipartFile testFile1;

    /**
     * SetUp test environment.
     *
     * @throws ExistsException                          ExistsException
     * @throws NotFoundException                        NotFoundException
     * @throws SQLIntegrityConstraintViolationException SQLIntegrityConstraintViolationException
     */
    @BeforeEach
    public void setUp() throws ExistsException, NotFoundException,
            SQLIntegrityConstraintViolationException, IOException {

        // arrange
        container = containerJpaService.register(new Container(34.0, "test", "desc"));
        container1 = new Container(43.0, "test2", "desc2");
        containerDto = modelMapper.map(container, ContainerDto.class);

        csv = csvJpaService.register(new Csv(), containerDto);
        csv1 = new Csv();
        csvdto = modelMapper.map(csv, CsvDto.class);

        record = recordJpaService.register(new Record(), csvdto);
        record2 = recordJpaService.register(new Record(), csvdto);

        recordDto = modelMapper.map(record, RecordDto.class);
        record2Dto = modelMapper.map(record2, RecordDto.class);

        File file = new File("src/test/resources/detected.csv");
        FileInputStream input = new FileInputStream(file);

        testFile1 = new MockMultipartFile(
                "detected.csv", file.getName(),
                "application/octet-stream", input.readAllBytes());

        input.close();

    }

    /**
     * Register csv with container.
     */
    @Test
    public void registerCsvWithContainerTest() {

        // act
        csv = csvJpaService.findById(csv.getId());

        // assert
        assertThat(csv.getContainer()).isNotNull();
        assertThat(container.getCsv()).isEqualTo(csv);
    }

    /**
     * Register multiple containers.
     */
    @Test
    public void registerMultipleCsvWithContainerTest() {

        // act and assert
        assertThrows(SQLIntegrityConstraintViolationException.class, () -> {
            csvJpaService.register(csv1, containerDto);
        });
    }

    /**
     * Register multiple Csvs with different containers.
     *
     * @throws ExistsException                          ExistsException
     * @throws NotFoundException                        NotFoundException
     * @throws SQLIntegrityConstraintViolationException SQLIntegrityConstraintViolationException
     */
    @Test
    public void registerMultipleCsvWithDifferentContainerTest() throws ExistsException,
            NotFoundException, SQLIntegrityConstraintViolationException {

        // arrange
        container1 = containerJpaService.register(container1);
        ContainerDto containerDto1 = modelMapper.map(container1,
                ContainerDto.class);

        // act
        csv1 = csvJpaService.register(csv1, containerDto1);

        // assert
        assertThat(csv1.getContainer()).isEqualTo(container1);
        assertThat(csv.getContainer()).isEqualTo(container);

        container = containerJpaService
                .findById(container.getId());
        container1 = containerJpaService
                .findById(container1.getId());
        assertThat(container.getCsv()).isEqualTo(csv);
        assertThat(container1.getCsv()).isEqualTo(csv1);
    }

    /**
     * Register already existing csv.
     */
    @Test
    public void registerAlreadyExistingCsvTest() {

        // act and assert
        assertThrows(ExistsException.class, () -> {
            csvJpaService.register(csv, containerDto);
        });
    }

    /**
     * Get records in csv test.
     */
    @Test
    public void getRecordsInCsvTest() {

        // act and assert
        assertThrows(NotFoundException.class, () -> {
            csvJpaService.getRecordsInCsv(new CsvDto(10000L));
        });
    }

    /**
     * Get records in csv test.
     */
    @Test
    public void deleteRecordsInCsvTest() {

        // act and assert
        assertThrows(NotFoundException.class, () -> {
            csvJpaService.deleteRecordsInCsv(new CsvDto(10000L));
        });
    }

    /**
     * Register existing csv with another container.
     *
     * @throws ExistsException ExistsException
     */
    @Test
    public void registerExistingWithAnotherContainerTest()
            throws ExistsException {

        // arrange
        containerJpaService.register(container1);
        ContainerDto containerDto1 =
                modelMapper.map(container1, ContainerDto.class);

        // act and assert
        assertThrows(ExistsException.class, () -> {
            csvJpaService.register(csv, containerDto1);
        });
    }

    /**
     * Register new csv with a non existing container.
     */
    @Test
    public void registerNewCSvNonExistingContainerTest() {

        // arrange
        ContainerDto containerDto1 =
                modelMapper.map(container1, ContainerDto.class);

        // act and assert
        assertThrows(NotFoundException.class, () -> {
            csvJpaService.register(csv1, containerDto1);
        });
    }

    /**
     * Update is not supported.
     */
    @Test
    public void updateCsvUnsupportedOperationTest() {

        // act and assert
        assertThrows(UnsupportedOperationException.class, () -> {
            csvJpaService.update(csvdto, csvdto);
        });
    }


    /**
     * Deleting csv does not delete container.
     *
     * @throws NotFoundException NotFoundException
     */
    @Test
    public void deleteCsvNotDeleteContainerTest() throws NotFoundException {

        // act
        csvJpaService.delete(csvdto);

        // assert
        assertThat(csvJpaService.findById(csv.getId())).isNull();
        assertThat(containerJpaService.findById(csvdto.getContainerId())).isNotNull();
        assertThat(recordJpaService.findById(record.getId())).isNull();
        assertThat(recordJpaService.findById(record2.getId())).isNull();
        assertThat(csvJpaService.count()).isEqualTo(0);
        assertThat(recordJpaService.count()).isEqualTo(0);
        assertThat(containerJpaService.count()).isEqualTo(1);
    }

    /**
     * Delete csv out preremove.
     *
     * @throws ExistsException   ExistsException
     * @throws NotFoundException NotFoundException
     */
    @Test
    public void removeCsvWithoutContainerTest() throws ExistsException,
            NotFoundException, IOException {

        // arrange
        Csv standAlone = csvJpaService.register(new Csv());

        // act
        csvJpaService.delete(new CsvDto(standAlone));

        recordJpaService.save(testFile1, csvdto);
        csv = csvJpaService.findById(csvdto.getId());

        // assert
        assertThat(csvJpaService.count()).isEqualTo(1);
    }


}
