package nl.tudelft.sp.modelchecker.database;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import javassist.NotFoundException;
import nl.tudelft.sp.modelchecker.Application;
import nl.tudelft.sp.modelchecker.dto.*;
import nl.tudelft.sp.modelchecker.entities.*;
import nl.tudelft.sp.modelchecker.entities.Record;
import nl.tudelft.sp.modelchecker.exceptions.DateException;
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
public class DatabaseContainerTest {

    @Autowired
    ContainerJpaService containerJpaService;


    @Autowired
    CsvJpaService csvJpaService;

    @Autowired
    PersistentCsvJpaService persistentCsvJpaService;

    @Autowired
    PersistentRecordJpaService persistentRecordJpaService;

    @Autowired
    RecordJpaService recordJpaService;

    @Autowired
    AdminJpaService adminJpaService;

    @Autowired
    ProjectJpaService projectJpaService;

    @Autowired
    SubmissionJpaService submissionJpaService;

    @Autowired
    ModelMapper modelMapper;

    private Admin admin;
    private UserDto adminDto;

    private Container container;
    private Container container1;

    private ContainerDto containerDto;
    private ContainerDto container1Dto;

    private Project project;
    private Project project1;

    private ProjectDto projectDto;
    private ProjectDto projectDto1;

    private Csv csv;

    private MultipartFile multipartFileCsv;
    private MultipartFile multipartFileClasses;


    /**
     * Set Up test environment.
     *
     * @throws ExistsException                          ExistsException
     * @throws NotFoundException                        NotFoundException
     * @throws IOException                              IOException
     * @throws SQLIntegrityConstraintViolationException SQLIntegrityConstraintViolationException
     */
    @BeforeEach
    public void setUp() throws ExistsException, NotFoundException, IOException,
            SQLIntegrityConstraintViolationException, DateException {

        // arrange
        admin = new Admin("1A", "akash", "amalan_akash@hotmail.com",
                Date.valueOf(LocalDate.now()));

        admin = adminJpaService.registerUser(admin);
        adminDto = modelMapper.map(admin, UserDto.class);

        project = projectJpaService.register(new Project("title", "description"));
        projectDto = modelMapper.map(project, ProjectDto.class);

        project1 = new Project("title2", "description2");
        projectDto1 = modelMapper.map(project1, ProjectDto.class);

        container = containerJpaService.register(
                new Container(42.0, "test", "test container"), projectDto);

        containerDto = modelMapper.map(container, ContainerDto.class);

        container1 = containerJpaService.register(
                new Container(52.0, "test2", "test container2"), projectDto);
        container1Dto = modelMapper.map(container1, ContainerDto.class);

        csv = csvJpaService.register(new Csv(), containerDto);


        File csvFile = new File("src/test/resources/detected.csv");
        FileInputStream input2 = new FileInputStream(csvFile);

        File textFile = new File("src/test/resources/classes.txt");
        FileInputStream input3 = new FileInputStream(textFile);

        multipartFileCsv = new MockMultipartFile(
                "detected.csv", "detected.csv",
                "application/octet-stream", input2.readAllBytes());


        multipartFileClasses = new MockMultipartFile(
                "classes.txt", "classes.txt",
                "text/plain", input3.readAllBytes()
        );

        input2.close();
        input3.close();
    }

    /**
     * Register container with Project.
     */
    @Test
    public void registerContainerWithProjectTest() {

        // act
        container = containerJpaService.findById(container.getId());
        project = projectJpaService.findById(project.getId());

        // assert
        assertThat(container.getProject()).isEqualTo(project);
        assertThat(project.getContainers().toArray()[1]).isEqualTo(container);

    }

    /**
     * Register container with different blob.
     */
    @Test
    public void registerContainerWithDifferentBlobAndCsvTest() {

        // assert
        assertThat(container1.getProject()).isEqualTo(project);
        assertThat(project.getContainers().size()).isEqualTo(2);

        assertThat(project.getContainers().toArray()[1]).isEqualTo(container);
        assertThat(project.getContainers().toArray()[0]).isEqualTo(container1);
    }

    /**
     * Register non existing container.
     */
    @Test
    public void registerNotExistingTest() {

        // act and assert
        assertThrows(ExistsException.class, () -> {
            containerJpaService.register(container, projectDto);
        });
    }

    /**
     * Register with a non existing project.
     */
    @Test
    public void registerProjectNotFoundTest() {

        // act and assert
        assertThrows(NotFoundException.class, () -> {
            containerJpaService.register(new Container(), projectDto1);
        });
    }

    /**
     * Update the container.
     *
     * @throws NotFoundException NotFoundException
     */
    @Test
    public void updateContainerTest() throws NotFoundException {

        // act
        containerJpaService.update(containerDto, container1Dto);

        // assert
        assertThat(containerJpaService.findById(containerDto.getId()).getFrameRate())
                .isEqualTo(container1Dto.getFrameRate());
        assertThat(containerJpaService.findById(containerDto.getId()).getDescription())
                .isEqualTo(container1Dto.getDescription());
        assertThat(containerJpaService.findById(containerDto.getId()).getName())
                .isEqualTo(container1Dto.getName());
    }

    /**
     * Update container which does not exist.
     */
    @Test
    public void updateContainerDoesNotExistTest() {

        // act and assert
        assertThrows(NotFoundException.class, () -> {
            containerJpaService.update(new ContainerDto(), containerDto);
        });
    }

    /**
     * Remove container should remove children.
     *
     * @throws NotFoundException                        NotFoundException
     * @throws ExistsException                          ExistsException
     * @throws SQLIntegrityConstraintViolationException SQLIntegrityConstraintViolationException
     */
    @Test
    public void removeContainerRemoveCsvAndBlobTest()
            throws NotFoundException, ExistsException,
            SQLIntegrityConstraintViolationException {

        // act
        containerJpaService.delete(containerDto);
        Container container = containerJpaService.findById(containerDto.getId());

        // assert
        assertThat(container).isNull();

        // arrange
        csvJpaService.register(new Csv(), container1Dto);

        assertThat(containerJpaService.count()).isEqualTo(1);
        assertThat(csvJpaService.count()).isEqualTo(1);

        // act
        containerJpaService.delete(container1Dto);

        // assert
        assertThat(containerJpaService.count()).isEqualTo(0);
        assertThat(csvJpaService.count()).isEqualTo(0);
    }


    /**
     * delete container does not delete project but blob, csv.
     *
     * @throws NotFoundException NotFoundException
     */
    @Test
    public void deleteContainerDoesNotDeleteProject() throws NotFoundException {

        // act
        containerJpaService.delete(containerDto);
        container = containerJpaService.findById(container.getId());

        // assert
        assertThat(container).isNull();
        assertThat(projectJpaService.findById(project.getId())).isNotNull();
        assertThat(csvJpaService.findById(csv.getId())).isNull();
        assertThat(containerJpaService.count()).isEqualTo(1);
        assertThat(csvJpaService.count()).isEqualTo(0);
        assertThat(projectJpaService.count()).isEqualTo(1);
    }

    /**
     * test fill container method.
     *
     * @throws Exception Exception
     */
    @Test
    public void fillContainerTest() throws Exception {

        // arrange
        Container container2 = new Container(35.0,
                "test", "desc");

        // act
        container2 = containerJpaService.fillContainer(
                multipartFileCsv, multipartFileClasses,
                container2, projectDto);

        container2 = containerJpaService.findById(container2.getId());

        // assert
        assertThat(container2).isNotNull();

        assertThat(containerJpaService.count()).isEqualTo(3);
        assertThat(container2.getCsv().getRecords().size()).isEqualTo(451);
        assertThat(container2.getPersistentCSv().getPersistentRecords().size()).isEqualTo(451);
        assertThat(container2.getClasses().size()).isEqualTo(13);
        assertThat(csvJpaService.count()).isEqualTo(2);
        assertThat(recordJpaService.count()).isEqualTo(451);
        assertThat(persistentRecordJpaService.count()).isEqualTo(451);
    }

    /**
     * test override container.
     *
     * @throws Exception Exception
     */
    @Test
    public void overideContainerTest() throws Exception {

        // arrange
        Container container2 = new Container(35.0,
                "test", "desc");

        container2 = containerJpaService.fillContainer(
                multipartFileCsv, multipartFileClasses,
                container2, projectDto);

        Long csvId = container2.getCsv().getId();
        Csv returnedCsv = csvJpaService.findById(csvId);
        Record randomRecord = returnedCsv.getRecords().stream().findAny().get();

        int objectId = randomRecord.getObjectId();


        Record record2 = Record.builder().frameNum(22)
                .objectId(1).trackerL(32).label("testLabel100")
                .trackerH(3).trackerW(53).trackerT(3).build();

        RecordDto record2Dto = modelMapper.map(record2, RecordDto.class);
        record2Dto.setObjectId(objectId);
        record2Dto.setCsvId(returnedCsv.getId());

        Record record3 = Record.builder().frameNum(2)
                .objectId(100000).trackerL(62).label("testLabel200")
                .trackerH(33).trackerW(7).trackerT(312).build();

        RecordDto record3Dto = modelMapper.map(record3, RecordDto.class);
        record3Dto.setCsvId(returnedCsv.getId());

        List<RecordDto> recordDtos = Arrays.asList(record2Dto, record3Dto);
        RecordListDto recordListDto = new RecordListDto(recordDtos, container2.getId());

        // act
        containerJpaService.overideContainer(recordListDto);

        returnedCsv = csvJpaService.findById(csvId);


        // assert
        long randomId = randomRecord.getId();
        assertThat(returnedCsv.getRecords().size()).isEqualTo(452);
        assertThat(recordJpaService.findById(randomId).getLabel()).isEqualTo(record2.getLabel());
    }
}
