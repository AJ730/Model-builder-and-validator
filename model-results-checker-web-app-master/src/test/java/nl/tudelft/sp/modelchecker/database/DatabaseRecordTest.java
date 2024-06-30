package nl.tudelft.sp.modelchecker.database;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
public class DatabaseRecordTest {

    @Autowired
    RecordJpaService recordJpaService;

    @Autowired
    CsvJpaService csvjpaService;

    @Autowired
    ContainerJpaService containerJpaService;

    @Autowired
    ModelMapper modelMapper;

    private Record record;
    private Record record1;

    private Csv csv;
    private CsvDto csvdto;
    private RecordDto recordDto;
    private RecordDto recordDto1;

    private MultipartFile testFile1;
    private MultipartFile testFile2;

    /**
     * Set Up test environment.
     *
     * @throws ExistsException   ExistsException
     * @throws NotFoundException NotFoundException
     */
    @BeforeEach
    public void setUp() throws ExistsException, NotFoundException, IOException {

        // arrange
        record = Record.builder().frameNum(2)
                .objectId(1).trackerL(3).label("testLabel100")
                .trackerH(3).trackerW(5).trackerT(3).build();


        record1 = Record.builder().frameNum(2)
                .objectId(4).trackerL(6).label("testLabel200")
                .trackerH(3).trackerW(7).trackerT(3).build();

        csv = new Csv();

        csv = csvjpaService.register(csv);
        csvdto = modelMapper.map(csv, CsvDto.class);
        csv = csvjpaService.findById(csv.getId());

        record = recordJpaService.register(record, csvdto);
        recordDto = modelMapper.map(record, RecordDto.class);
        recordDto1 = modelMapper.map(record1, RecordDto.class);

        File file = new File("src/test/resources/detected.csv");
        FileInputStream input = new FileInputStream(file);

        testFile1 = new MockMultipartFile(
                "detected.csv", file.getName(),
                "application/octet-stream", input.readAllBytes());

        input.close();

        File file2 = new File("src/test/resources/detected2.csv");
        FileInputStream input2 = new FileInputStream(file2);

        testFile2 = new MockMultipartFile(
                "detected.csv", file2.getName(),
                "application/octet-stream", input2.readAllBytes());

        input2.close();

    }

    /**
     * Register a record.
     */
    @Test
    public void testRegisterOneRecordTest() {

        // assert
        assertThat(recordJpaService.findById(record.getId())).isNotNull();
        assertThat(csv.getRecords().size()).isEqualTo(1);
    }

    /**
     * Register multiple records same csv.
     *
     * @throws NotFoundException NotFoundException
     * @throws ExistsException   ExistsException
     */
    @Test
    public void testRegisterMultipleSameRecordsSameCSvTest()
            throws NotFoundException, ExistsException {

        // arrange
        Record newRecord = Record.builder().frameNum(2)
                .objectId(1).trackerL(3).label("label")
                .trackerH(3).trackerW(5).trackerT(3).build();

        // act
        recordJpaService.register(newRecord, csvdto);

        // assert
        assertThat(recordJpaService.count()).isEqualTo(2);
        assertThat(csv.getRecords().size()).isEqualTo(2);
    }

    /**
     * Register different records same csv.
     *
     * @throws NotFoundException NotFoundException
     * @throws ExistsException   ExistsException
     */
    @Test
    public void testRegisterDifferentRecordsSameCsvTest()
            throws NotFoundException, ExistsException {

        // arrange
        Record newRecord = Record.builder().frameNum(3)
                .objectId(4).trackerL(3).label("test")
                .trackerH(2).trackerW(6).trackerT(5).build();

        // act
        recordJpaService.register(newRecord, csvdto);

        // assert
        assertThat(recordJpaService.count()).isEqualTo(2);
        assertThat(csv.getRecords().size()).isEqualTo(2);
    }

    /**
     * Register same record different csv.
     *
     * @throws ExistsException   ExistsException
     * @throws NotFoundException NotFoundException
     */
    @Test
    public void testRegisterSameRecordDifferentCsvTest()
            throws ExistsException, NotFoundException {

        // arrange
        Csv csvTest = new Csv();
        csvjpaService.register(csvTest);
        CsvDto csvTestDto = modelMapper.map(csvTest, CsvDto.class);

        csvTest = csvjpaService.findById(csvTest.getId());

        // act
        record1 = recordJpaService.register(record1, csvTestDto);

        // assert
        assertThat(recordJpaService
                .findById(record1.getId())
                .getCsv()).isEqualTo(csvTest);

        assertThat(csvTest.getRecords().size()).isEqualTo(1);

        assertThat(recordJpaService
                .findById(record.getId())
                .getCsv()).isEqualTo(csv);

        assertThat(csv.getRecords().size()).isEqualTo(1);
    }

    /**
     * Register already existing record.
     */
    @Test
    public void testRegisterAlreadyExistingRecordTest() {

        // act and assert
        assertThrows(ExistsException.class, () ->
                recordJpaService.register(record, csvdto));
    }

    /**
     * Register a csv which does not exist.
     */
    @Test
    public void testRegisterCsvNotFoundTest() {

        // arrange
        Csv csv2 = new Csv();
        CsvDto csv2Dto = modelMapper.map(csv2, CsvDto.class);

        // act and assert
        assertThrows(NotFoundException.class, () ->
                recordJpaService.register(record1, csv2Dto));
    }

    /**
     * Delete a record without a csv.
     *
     * @throws NotFoundException NotFoundException
     */
    @Test
    public void deleteRecordWithoutCsvTest() throws NotFoundException {

        // act
        recordJpaService.delete(recordDto);

        // assert
        assertThat(recordJpaService.findById(recordDto.getId())).isNull();
        assertThat(csvjpaService.findById(csvdto.getId())).isNotNull();
        csv = csvjpaService.findById(csvdto.getId());
        assertThat(csv.getRecords().size()).isEqualTo(0);

        assertThat(recordJpaService.findAll().size()).isEqualTo(0);
    }

    /**
     * Delete a record which is not registered.
     */
    @Test
    public void deleteRecordNotFoundTest() {

        // act and assert
        assertThrows(NotFoundException.class, () -> {
            recordJpaService.delete(recordDto1);
        });
    }

    /**
     * Delete a record with a null Dto.
     */
    @Test
    public void deleteRecordNullDtoTest() {

        // act and assert
        assertThrows(NotFoundException.class, () -> {
            recordJpaService.delete((RecordDto) null);
        });
    }

    /**
     * Delete record with no dtoId.
     */
    @Test
    public void deleteRecordNoDtoIdTest() {

        // act and assert
        assertThrows(NotFoundException.class, () -> {
            recordJpaService.delete(new RecordDto());
        });
    }

    /**
     * Update Record.
     *
     * @throws NotFoundException NotFoundException
     */
    @Test
    public void updateRecordTest() throws NotFoundException {

        // arrange
        recordDto1.setObjectId(recordDto.getObjectId());
        recordDto1.setCsvId(recordDto.getCsvId());

        // act
        recordJpaService.update(recordDto, recordDto1);

        // assert
        assertThat(recordJpaService.findById(recordDto.getId()))
                .isEqualToIgnoringGivenFields(recordDto1,
                        "id", "csv");

    }

    /**
     * Update record not found.
     */
    @Test
    public void updateRecordNotFound() {

        // arrange
        recordDto1.setCsvId(csv.getId() + 1);

        // act and assert
        assertThrows(NotFoundException.class, () ->
                recordJpaService.update(recordDto1, recordDto));
    }

    /**
     * Save a MultiPartFile.
     *
     * @throws IOException       IOException
     * @throws ExistsException   ExistsException
     * @throws NotFoundException NotFoundException
     */
    @Test
    public void saveTest() throws IOException, ExistsException, NotFoundException {

        // act
        recordJpaService.save(testFile1, csvdto);

        // assert
        assertThat(recordJpaService.count()).isEqualTo(451);
        assertThat(csvjpaService.count()).isEqualTo(1);
    }

    /**
     * save records in same csv test.
     *
     * @throws ExistsException   ExistsException
     * @throws NotFoundException NotFoundException
     * @throws IOException       IOException
     */
    @Test
    public void saveRecordListSameCsvTest() throws ExistsException, NotFoundException, IOException {

        // arrange
        assertThat(recordJpaService.count()).isEqualTo(1);
        List<RecordDto> recordDtos = Arrays.asList(recordDto1, recordDto);

        // act
        recordJpaService.save(recordDtos, csvdto);

        // assert
        assertThat(recordJpaService.count()).isEqualTo(2);

        // act
        recordJpaService.overrideCsvCheck(csvdto);

        // assert
        assertThat(recordJpaService.count()).isEqualTo(0);
    }

    /**
     * save multiple record list in the same csv test.
     *
     * @throws ExistsException   ExistsException
     * @throws NotFoundException NotFoundException
     * @throws IOException       IOException
     */
    @Test
    public void saveMultipleRecordListSameCsvTest()
            throws ExistsException, NotFoundException, IOException {

        // arrange
        record1 = recordJpaService.register(record1, csvdto);
        recordDto1 = modelMapper.map(record1, RecordDto.class);
        List<RecordDto> recordDtos = Arrays.asList(recordDto1, recordDto);

        // act
        recordJpaService.save(recordDtos, csvdto);

        // assert
        assertThat(recordJpaService.count()).isEqualTo(2);

        // act
        recordJpaService.save(recordDtos, csvdto);

        // assert
        assertThat(recordJpaService.count()).isEqualTo(2);
    }

    /**
     * save same records in different csv test.
     *
     * @throws ExistsException   ExistsException
     * @throws NotFoundException NotFoundException
     * @throws IOException       IOException
     */
    @Test
    public void saveSameRecordsListDifferentCsvTest()
            throws ExistsException, NotFoundException, IOException {

        // arrange
        List<RecordDto> recordDtos = Collections.singletonList(recordDto);

        // act
        recordJpaService.save(recordDtos, csvdto);

        // assert
        assertThat(recordJpaService.count()).isEqualTo(1);

        // arrange
        Csv csv2 = new Csv();
        csv2 = csvjpaService.register(csv2);
        CsvDto csv2dto = modelMapper.map(csv2, CsvDto.class);
        record1 = recordJpaService.register(record1, csv2dto);
        recordDto1 = modelMapper.map(record1, RecordDto.class);
        List<RecordDto> recordDtos1 = Collections.singletonList(recordDto1);

        // act
        recordJpaService.save(recordDtos1, csvdto);

        // assert
        assertThat(recordJpaService.count()).isEqualTo(2);
        assertThat(csvjpaService.count()).isEqualTo(2);

        Record updated = (Record) csvjpaService.findById(csv.getId()).getRecords().toArray()[0];
        Record updated1 = (Record) csvjpaService.findById(csv2.getId()).getRecords().toArray()[0];

        assertThat(updated).isEqualToIgnoringGivenFields(record);
        assertThat(updated1).isEqualToIgnoringGivenFields(record1);

        // act
        recordJpaService.overrideCsvCheck(csvdto);
        recordJpaService.overrideCsvCheck(csv2dto);

        // assert
        assertThat(recordJpaService.count()).isEqualTo(0);
        assertThat(csvjpaService.count()).isEqualTo(2);
    }

    /**
     * save multiple records in different csv test.
     *
     * @throws ExistsException   ExistsException
     * @throws NotFoundException NotFoundException
     * @throws IOException       IOException
     */
    @Test
    public void saveMultipleRecordsInDifferentCsvTest()
            throws ExistsException, NotFoundException, IOException {

        // arrange
        Csv csv2 = new Csv();
        csv2 = csvjpaService.register(csv2);

        // act
        record1 = recordJpaService.register(record1, csvdto);
        recordDto1 = modelMapper.map(record1, RecordDto.class);

        List<RecordDto> recordList1Dto = Arrays.asList(recordDto, recordDto1);

        // assert
        assertThat(csv.getRecords().toArray()[1]).isEqualToIgnoringGivenFields(record,
                "id", "csv");
        assertThat(csv.getRecords().toArray()[0]).isEqualToIgnoringGivenFields(record1,
                "id", "csv");

        // arrange
        Record record2 = Record.builder().frameNum(2)
                .objectId(1).trackerL(5).label("testLabel1")
                .trackerH(32).trackerW(9).trackerT(3).build();

        RecordDto recordDto2 = modelMapper.map(record2, RecordDto.class);

        Record record3 = Record.builder().frameNum(2)
                .objectId(5).trackerL(6).label("testLabel2")
                .trackerH(3).trackerW(7).trackerT(3).build();

        RecordDto recordDto3 = modelMapper.map(record3, RecordDto.class);

        List<RecordDto> recordList2Dto = Arrays.asList(recordDto2, recordDto3);
        CsvDto csv2dto = modelMapper.map(csv2, CsvDto.class);

        // act
        recordJpaService.save(recordList2Dto, csv2dto);

        // assert
        assertThat(recordJpaService.count()).isEqualTo(4);
        assertThat(csvjpaService.count()).isEqualTo(2);

        csv2 = csvjpaService.findById(csv2.getId());
        assertThat(csv2.getRecords().size()).isEqualTo(2);
        assertThat(csv2.getRecords().toArray()[0]).isEqualToIgnoringGivenFields(record3,
                "id", "csv");
        assertThat(csv2.getRecords().toArray()[1]).isEqualToIgnoringGivenFields(record2,
                "id", "csv");

    }

    /**
     * save and update records test.
     *
     * @throws ExistsException   ExistsException
     * @throws NotFoundException NotFoundException
     * @throws IOException       IOException
     */
    @Test
    public void saveUpdateTest() throws ExistsException, NotFoundException, IOException {

        // arrange
        assertThat(recordJpaService.count()).isEqualTo(1);
        recordDto1 = modelMapper.map(record1, RecordDto.class);
        recordDto.setLabel("changedLabel");
        recordDto1.setLabel("newLabel");

        List<RecordDto> recordDtos = Arrays.asList(recordDto, recordDto1);

        // act
        recordJpaService.save(recordDtos, csvdto);
        record = recordJpaService.findById(recordDto.getId());
        record1 = recordJpaService.findAll().get(1);

        // assert
        assertThat(recordJpaService.count()).isEqualTo(2);

        assertThat(record1.getLabel()).isEqualTo("newLabel");
        assertThat(record.getLabel()).isEqualTo("changedLabel");

        // arrange
        recordDto.setLabel("returnedLabel");
        recordDto1 = modelMapper.map(record1, RecordDto.class);

        recordDto1.setLabel("changedLabel");
        recordDtos = Arrays.asList(recordDto1, recordDto);

        // act
        recordJpaService.save(recordDtos, csvdto);

        // assert
        record = recordJpaService.findById(recordDto.getId());
        assertThat(recordJpaService.count()).isEqualTo(2);
        record1 = recordJpaService.findById(record1.getId());
        assertThat(record.getLabel()).isEqualTo("returnedLabel");
        assertThat(record1.getLabel()).isEqualTo("changedLabel");
    }


    /**
     * test save multiple times the same csv.
     *
     * @throws IOException       IOException
     * @throws ExistsException   ExistsException
     * @throws NotFoundException NotFoundException
     */
    @Test
    public void saveMultipleTimesSameCSvNoChange() throws IOException,
            ExistsException, NotFoundException {
        // act
        recordJpaService.save(testFile1, csvdto);

        // assert
        assertThat(recordJpaService.count()).isEqualTo(451);

        List<RecordDto> recordDtos = csvjpaService.getRecordsInCsv(csvdto);

        // act
        recordJpaService.save(testFile1, csvdto);

        // assert
        assertThat(recordJpaService.count()).isEqualTo(451);
        List<RecordDto> overidenDtos = csvjpaService.getRecordsInCsv(csvdto);

        assertThat(recordDtos.size()).isEqualTo(451);
        assertThat(overidenDtos.size()).isEqualTo(451);
        assertThat(csvjpaService.count()).isEqualTo(1);
    }

    /**
     * test save different records in different csv.
     *
     * @throws ExistsException   ExistsException
     * @throws NotFoundException NotFoundException
     * @throws IOException       IOException
     */
    @Test
    public void saveDifferentRecordsInDifferentCSv()
            throws ExistsException, NotFoundException, IOException {

        // arrange
        Csv csv2 = new Csv();
        csv2 = csvjpaService.register(csv2);
        CsvDto newCsvDto = new CsvDto(csv2);

        // act
        recordJpaService.save(testFile2, newCsvDto);
        csv2 = csvjpaService.findById(csv2.getId());

        // assert
        assertThat(csv2.getRecords().size()).isEqualTo(399);
        assertThat(recordJpaService.count()).isEqualTo(400);

        // act
        recordJpaService.save(testFile1, csvdto);
        csv = csvjpaService.findById(csvdto.getId());

        // assert
        assertThat(csv.getRecords().size()).isEqualTo(451);
        assertThat(recordJpaService.count()).isEqualTo(850);
    }

    /**
     * test save same records in different csv.
     *
     * @throws ExistsException   ExistsException
     * @throws NotFoundException NotFoundException
     * @throws IOException       IOException
     */
    @Test
    public void saveSameRecordsInDifferentCSv()
            throws ExistsException, NotFoundException, IOException {

        // act
        recordJpaService.save(testFile1, csvdto);
        csv = csvjpaService.findById(csvdto.getId());

        // assert
        assertThat(csv.getRecords().size()).isEqualTo(451);

        // arrange
        Csv csv2 = new Csv();
        csv2 = csvjpaService.register(csv2);
        CsvDto differentDto = new CsvDto(csv2);

        // act
        recordJpaService.save(testFile1, differentDto);
        csv2 = csvjpaService.findById(differentDto.getId());

        // assert
        assertThat(csv2.getRecords().size()).isEqualTo(451);
    }

    /**
     * test delete record does not delete csv.
     *
     * @throws NotFoundException NotFoundException
     */
    @Test
    public void deleteRecordDoesNotDeleteCsv() throws NotFoundException {

        // act
        recordJpaService.delete(recordDto);

        // assert
        assertThat(recordJpaService.findById(record.getId())).isNull();
        assertThat(csvjpaService.findById(csv.getId())).isNotNull();
        assertThat(csvjpaService.count()).isEqualTo(1);
        assertThat(recordJpaService.count()).isEqualTo(0);
    }

    /**
     * test delete records in csv file.
     *
     * @throws ExistsException   ExistsException
     * @throws NotFoundException NotFoundException
     */
    @Test
    public void deleteRecordsInCsvTest() throws ExistsException, NotFoundException {

        // delete existing record
        // arrange
        record1 = recordJpaService.register(record1, csvdto);
        assertThat(recordJpaService.count()).isEqualTo(2);

        List<RecordDto> recordDtos = new ArrayList<>();
        recordDtos.add(recordDto);

        // act
        recordJpaService.delete(recordDtos);


        // assert
        assertThat(recordJpaService.count()).isEqualTo(1);
        assertThat(csvjpaService.getRecordsInCsv(csvdto).size()).isEqualTo(1);

        // delete nonexistent record
        // act
        recordJpaService.delete(recordDtos);
        assertThat(recordJpaService.count()).isEqualTo(1);
        assertThat(csvjpaService.getRecordsInCsv(csvdto).size()).isEqualTo(1);
    }

    /**
     * test save csv.
     *
     * @throws Exception Exception
     */
    @Test
    public void saveCsvTest() throws Exception {

        // arrange
        recordJpaService.delete(recordDto);
        Container container1 = containerJpaService.register(new Container(34.0,
                "test", "desc"));

        // act
        csv = csvjpaService.createCsvAndSaveRecords(new ContainerDto(container1), testFile1);

        csv = csvjpaService.findById(csv.getId());

        // assert
        assertThat(csv.getRecords().size()).isEqualTo(451);

        container1 = containerJpaService.findById(container1.getId());
        assertThat(container1.getCsv()).isEqualTo(csv);
        assertThat(container1.getCsv().getRecords().size()).isEqualTo(451);

        assertThat(recordJpaService.count()).isEqualTo(451);
    }
}
