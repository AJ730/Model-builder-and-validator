package nl.tudelft.sp.modelchecker.database;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.SQLIntegrityConstraintViolationException;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Transactional
public class DatabasePersistentCsvTest {


    @Autowired
    CsvJpaService csvJpaService;

    @Autowired
    PersistentCsvJpaService persistentCsvJpaService;

    @Autowired
    PersistentRecordJpaService persistentRecordJpaService;

    @Autowired
    ContainerJpaService containerJpaService;

    @Autowired
    RecordJpaService recordJpaService;

    @Autowired
    ModelMapper modelMapper;

    private Csv csv;
    private CsvDto csvdto;

    private PersistentCsv persistentCsv;
    private CsvDto persistentCsvDto;
    private PersistentCsv persistentCsv1;

    private Container container;
    private Container container1;
    private ContainerDto containerDto;

    private Record record;
    private PersistentRecord persistentRecord1;
    private PersistentRecord persistentRecord2;

    private RecordDto recordDto;
    private RecordDto recordPersistent1Dto;
    private RecordDto recordPersistent2Dto;

    /**
     * SetUp test environment.
     *
     * @throws ExistsException                          ExistsException
     * @throws NotFoundException                        NotFoundException
     * @throws SQLIntegrityConstraintViolationException SQLIntegrityConstraintViolationException
     */
    @BeforeEach
    public void setUp() throws ExistsException, NotFoundException,
            SQLIntegrityConstraintViolationException {

        // arrange
        container = containerJpaService.register(new Container(34.0,
                "test", "desc"));
        container1 = new Container(43.0, "test2", "desc2");
        containerDto = modelMapper.map(container, ContainerDto.class);

        csv = csvJpaService.register(new Csv(), containerDto);
        csvdto = modelMapper.map(csv, CsvDto.class);

        persistentCsv = persistentCsvJpaService.register(new PersistentCsv(), containerDto);
        persistentCsvDto = modelMapper.map(persistentCsv, CsvDto.class);

        persistentCsv1 = new PersistentCsv();

        record = recordJpaService.register(new Record(), csvdto);
        recordDto = modelMapper.map(record, RecordDto.class);

        persistentRecord1 = persistentRecordJpaService
                .register(new PersistentRecord(), persistentCsvDto);
        persistentRecord2 = persistentRecordJpaService
                .register(new PersistentRecord(), persistentCsvDto);

        recordPersistent1Dto = modelMapper.map(persistentRecord1, RecordDto.class);
        recordPersistent2Dto = modelMapper.map(persistentRecord2, RecordDto.class);
    }

    /**
     * Register persistent csv with a container.
     */
    @Test
    public void registerPersistentCsvOneContainerTest() {

        // assert
        assertThat(csvJpaService.count()).isEqualTo(1);
        PersistentCsv returnCsv =
                persistentCsvJpaService.findById(persistentCsv.getId());

        assertThat(persistentCsvJpaService.count()).isEqualTo(1);
        assertThat(persistentRecordJpaService.count()).isEqualTo(2);
        assertThat(returnCsv.getPersistentRecords().size()).isEqualTo(2);
        assertThat(container.getPersistentCSv()).isEqualTo(returnCsv);
    }

    /**
     * Register an exsistent persistent csv.
     */
    @Test
    public void registerExistsPersistentCsvTest() {

        // act and assert
        assertThrows(ExistsException.class, () -> {
            persistentCsvJpaService.register(persistentCsv, containerDto);
        });
    }

    /**
     * Register using unfound container.
     */
    @Test
    public void notFoundContainerTest() {

        // act and assert
        assertThrows(NotFoundException.class, () -> {
            persistentCsvJpaService.register(persistentCsv1, new ContainerDto());
        });
    }

    /**
     * Cannot register multiple csvs in one container.
     */
    @Test
    public void cannotRegisterMultipleCsvsInOneContainerTest() {

        // act and assert
        assertThrows(SQLIntegrityConstraintViolationException.class, () -> {
            persistentCsvJpaService.register(persistentCsv1, containerDto);
        });
    }

    /**
     * Updating a persistent is not supported.
     */
    @Test
    public void persistentCsvUpdateTest() {

        // act and assert
        assertThrows(UnsupportedOperationException.class, () -> {
            persistentCsvJpaService.update(persistentCsvDto, new CsvDto());
        });
    }

    /**
     * Delete persistent csv does not delete a container.
     */
    @Test
    public void deletePersistentCsvDoesNotDeleteContainerTest() throws NotFoundException {

        // act
        persistentCsvJpaService.delete(persistentCsvDto);

        // assert
        assertThat(persistentCsvJpaService.count()).isEqualTo(0);
        assertThat(csvJpaService.count()).isEqualTo(1);
        assertThat(persistentRecordJpaService.count()).isEqualTo(0);
        assertThat(containerJpaService.count()).isEqualTo(1);
    }

    /**
     * Delete peristent csv without preremove.
     *
     * @throws ExistsException   ExistsException
     * @throws NotFoundException NotFoundException
     */
    @Test
    public void removeCsvWithoutContainerTest() throws ExistsException, NotFoundException {

        // arrange
        PersistentCsv standAlone = persistentCsvJpaService.register(new PersistentCsv());

        // act
        persistentCsvJpaService.delete(new CsvDto(standAlone));

        // assert
        assertThat(persistentCsvJpaService.count()).isEqualTo(1);
    }
}
