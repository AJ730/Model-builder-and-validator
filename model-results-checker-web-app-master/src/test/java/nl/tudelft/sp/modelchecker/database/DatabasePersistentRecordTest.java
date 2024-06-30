package nl.tudelft.sp.modelchecker.database;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import javassist.NotFoundException;
import nl.tudelft.sp.modelchecker.Application;
import nl.tudelft.sp.modelchecker.dto.ContainerDto;
import nl.tudelft.sp.modelchecker.dto.CsvDto;
import nl.tudelft.sp.modelchecker.dto.RecordDto;
import nl.tudelft.sp.modelchecker.entities.*;
import nl.tudelft.sp.modelchecker.entities.Record;
import nl.tudelft.sp.modelchecker.exceptions.ExistsException;
import nl.tudelft.sp.modelchecker.services.servicebeans.*;
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
public class DatabasePersistentRecordTest {

    @Autowired
    CsvJpaService csvJpaService;

    @Autowired
    PersistentCsvJpaService persistentCsvJpaService;

    @Autowired
    PersistentRecordJpaService persistentRecordJpaService;

    @Autowired
    ContainerJpaService containerJpaService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    RecordJpaService recordJpaService;

    private Csv csv;
    private CsvDto csvdto;

    private PersistentCsv persistentCsv;
    private CsvDto persistentCsvDto;

    private Container container;
    private ContainerDto containerDto;

    private Record record;
    private PersistentRecord persistentRecord1;
    private PersistentRecord persistentRecord2;

    private RecordDto recordDto;
    private RecordDto recordPersistent1Dto;
    private RecordDto recordPersistent2Dto;

    private MultipartFile testFile1;

    private String desc = "desc";
    private String test = "test";

    @BeforeEach
    void setUp() throws ExistsException,
            SQLIntegrityConstraintViolationException, NotFoundException, IOException {

        // arrange
        container = containerJpaService.register(new Container(34.0,
                test, desc));
        containerDto = modelMapper.map(container, ContainerDto.class);

        csv = csvJpaService.register(new Csv(), containerDto);
        csvdto = modelMapper.map(csv, CsvDto.class);

        persistentCsv = persistentCsvJpaService.register(new PersistentCsv(), containerDto);
        persistentCsvDto = modelMapper.map(persistentCsv, CsvDto.class);

        record = recordJpaService.register(new Record(), csvdto);
        recordDto = modelMapper.map(record, RecordDto.class);

        persistentRecord1 = persistentRecordJpaService
                .register(new PersistentRecord(), persistentCsvDto);
        persistentRecord2 = persistentRecordJpaService
                .register(new PersistentRecord(), persistentCsvDto);

        recordPersistent1Dto = modelMapper.map(persistentRecord1, RecordDto.class);
        recordPersistent2Dto = modelMapper.map(persistentRecord2, RecordDto.class);

        File file = new File("src/test/resources/detected.csv");
        FileInputStream input = new FileInputStream(file);

        testFile1 = new MockMultipartFile(
                "detected.csv", file.getName(),
                "application/octet-stream", input.readAllBytes());

        input.close();
    }

    /**
     * test register persistent record.
     */
    @Test
    public void registerPersistentRecordTest() {

        // assert
        assertThat(persistentRecordJpaService.count()).isEqualTo(2);
        persistentCsv = persistentCsvJpaService.findById(persistentCsv.getId());
        persistentRecord1 = persistentRecordJpaService.findById(persistentRecord1.getId());

        assertThat(persistentCsv.getPersistentRecords().size()).isEqualTo(2);
        assertThat(persistentRecord1.getPersistentCsv()).isEqualTo(persistentCsv);
    }

    /**
     * test already exist record.
     */
    @Test
    public void registerRecordAlreadyExistsTest() {

        // act and assert
        assertThrows(ExistsException.class, () -> {
            persistentRecordJpaService.register(persistentRecord1, persistentCsvDto);
        });
    }

    /**
     * test register record with non existent csv.
     */
    @Test
    public void registerRecordCsvDoesNotExistTest() {

        // act and assert
        assertThrows(NotFoundException.class, () -> {
            persistentRecordJpaService.register(new PersistentRecord(), new CsvDto());
        });
    }

    /**
     * test update record.
     *
     * @throws NotFoundException NotFoundException
     */
    @Test
    public void updateRecordTest() throws NotFoundException {

        // act
        persistentRecordJpaService.update(recordPersistent1Dto, recordPersistent2Dto);
        persistentRecord1 = persistentRecordJpaService.findById(recordPersistent1Dto.getId());
        recordPersistent1Dto = modelMapper.map(persistentRecord1, RecordDto.class);

        // assert
        assertThat(recordPersistent1Dto).isEqualToIgnoringGivenFields(recordPersistent2Dto,
                "id");
    }

    /**
     * test update nonexistent record.
     */
    @Test
    public void updateNotFoundTest() {

        // act and assert
        assertThrows(NotFoundException.class, () -> {
            persistentRecordJpaService.update(new RecordDto(), recordPersistent2Dto);
        });
    }

    /**
     * test delete record of csv.
     *
     * @throws NotFoundException NotFoundException
     */
    @Test
    public void deleteRecordDeletesInstanceFromCsvTest() throws NotFoundException {

        // act
        persistentRecordJpaService.delete(recordPersistent1Dto);
        persistentCsv = persistentCsvJpaService.findById(persistentCsv.getId());

        // assert
        assertThat(persistentCsv.getPersistentRecords().size()).isEqualTo(1);

        // act
        persistentRecordJpaService.delete(recordPersistent2Dto);
        persistentCsv = persistentCsvJpaService.findById(persistentCsv.getId());

        // assert
        assertThat(persistentCsv.getPersistentRecords().size()).isEqualTo(0);
        assertThat(persistentCsvJpaService.count()).isEqualTo(1);
    }

    /**
     * test save record.
     *
     * @throws ExistsException   ExistsException
     * @throws NotFoundException NotFoundException
     * @throws IOException       IOException
     */
    @Test
    public void savePersistentRecordTest() throws ExistsException, NotFoundException, IOException {

        // arrange
        persistentRecordJpaService.delete(recordPersistent1Dto);
        persistentRecordJpaService.delete(recordPersistent2Dto);

        recordJpaService.register(Record.builder().frameNum(2)
                .objectId(1).trackerL(3).label("testLabel100")
                .trackerH(3).trackerW(5).trackerT(3).build(), csvdto);

        // act
        persistentRecordJpaService.save(csvdto, persistentCsvDto);
        persistentCsv = persistentCsvJpaService.findById(persistentCsv.getId());

        // assert

        assertThat(persistentCsv.getPersistentRecords().size()).isEqualTo(2);
    }

    /**
     * test save record with big file.
     *
     * @throws ExistsException   ExistsException
     * @throws NotFoundException NotFoundException
     * @throws IOException       IOException
     */
    @Test
    public void savePersistentRecordBigFileTest()
            throws ExistsException, NotFoundException, IOException {

        // arrange
        persistentRecordJpaService.delete(recordPersistent1Dto);
        persistentRecordJpaService.delete(recordPersistent2Dto);

        // act
        recordJpaService.save(testFile1, csvdto);
        persistentRecordJpaService.save(csvdto, persistentCsvDto);

        persistentCsv = persistentCsvJpaService.findById(persistentCsv.getId());

        // assert
        assertThat(persistentCsv.getPersistentRecords().size()).isEqualTo(451);
    }

    /**
     * test save csv.
     *
     * @throws Exception Exception
     */
    @Test
    public void saveCsvTest() throws Exception {

        // arrange
        persistentRecordJpaService.delete(recordPersistent1Dto);
        persistentRecordJpaService.delete(recordPersistent2Dto);

        recordJpaService.save(testFile1, csvdto);

        Container container1 = containerJpaService.register(new Container(34.0,
                test, desc));

        // act
        persistentCsv = persistentCsvJpaService.saveCsv(new ContainerDto(container1), csvdto);

        persistentCsv = persistentCsvJpaService.findById(persistentCsv.getId());

        // assert
        assertThat(persistentCsv.getPersistentRecords().size()).isEqualTo(451);

        container1 = containerJpaService.findById(container1.getId());
        assertThat(container1.getPersistentCSv()).isEqualTo(persistentCsv);

        assertThat(recordJpaService.count()).isEqualTo(451);
        assertThat(persistentRecordJpaService.count()).isEqualTo(451);
    }


    /**
     * test convert records into dtos.
     *
     * @throws Exception Exception
     */
    @Test
    public void convertRecordsIntoDtoTest() throws Exception {

        // arrange
        recordJpaService.save(testFile1, csvdto);

        Container container1 = containerJpaService.register(new Container(34.0,
                test, desc));
        persistentCsv = persistentCsvJpaService.saveCsv(new ContainerDto(container1), csvdto);

        // act
        List<RecordDto> recordDtos = persistentCsvJpaService
                .getRecordsInPersistentCsv(new CsvDto(persistentCsv));

        // assert
        assertThat(recordDtos.size()).isEqualTo(451);
    }

    /**
     * test convert records into null persistent csv.
     *
     * @throws Exception Exception
     */
    @Test
    public void convertRecordsIntoDtoNullPersistentCsvTest() throws Exception {

        // arrange
        Container container1 = containerJpaService.register(new Container(34.0,
                test, desc));

        // act and assert
        assertThrows(NotFoundException.class, () -> {
            persistentCsvJpaService.getRecordsInPersistentCsv(
                    new CsvDto(100L));
        });
    }
}
