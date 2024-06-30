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
import nl.tudelft.sp.modelchecker.exceptions.AuthorityException;
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
public class DatabaseSubmissionTest {

    @Autowired
    ProjectHolderJpaService projectHolderJpaService;

    @Autowired
    BasicUserJpaService basicUserJpaService;

    @Autowired
    AdminJpaService adminJpaService;

    @Autowired
    ProjectJpaService projectJpaService;

    @Autowired
    ContainerJpaService containerJpaService;

    @Autowired
    CsvJpaService csvJpaService;

    @Autowired
    SubmissionJpaService submissionJpaService;

    @Autowired
    ModelMapper modelMapper;

    private BasicUser client;
    private UserDto clientDto;

    private Admin admin;
    private UserDto adminDto;

    private Container container;
    private ContainerDto containerDto;

    private Project project;
    private ProjectDto projectDto;

    private ProjectHolder projectHolder;
    private ProjectHolderDto projectHolderDto;


    private Csv csv;
    private CsvDto csvdto;

    private Submission submission;
    private SubmissionDto submissionDto;

    private MultipartFile multipartFileCsv;
    private MultipartFile multipartFileClasses;

    /**
     * Set environment of test.
     *
     * @throws ExistsException                          ExistsException
     * @throws NotFoundException                        NotFoundException
     * @throws SQLIntegrityConstraintViolationException SQLIntegrityConstraintViolationException
     * @throws IOException                              IOException
     * @throws AuthorityException                       AuthorityException
     */
    @BeforeEach
    public void setUp() throws ExistsException,
            NotFoundException, SQLIntegrityConstraintViolationException,
            IOException, AuthorityException, DateException {

        // arrange
        admin = new Admin("1A", "akash", "amalan_akash@hotmail.com",
                Date.valueOf(LocalDate.now()));
        client = new BasicUser("2A", "dean", "dean_nyugen@hotmail.com",
                Date.valueOf(LocalDate.now()));

        admin = adminJpaService.registerUser(admin);
        client = basicUserJpaService.registerUser(client);

        adminDto = modelMapper.map(admin, UserDto.class);
        clientDto = modelMapper.map(client, UserDto.class);

        projectHolder = projectHolderJpaService.register(new ProjectHolder(), clientDto);
        projectHolderDto = modelMapper.map(projectHolder, ProjectHolderDto.class);

        project = projectJpaService.register(new Project("title", "description"),
                projectHolderDto, adminDto);
        projectDto = modelMapper.map(project, ProjectDto.class);

        container = containerJpaService.register(
                new Container(42.0, "test", "testContainer"), projectDto);
        containerDto = modelMapper.map(container, ContainerDto.class);

        csv = csvJpaService.register(new Csv(), containerDto);
        csvdto = modelMapper.map(csv, CsvDto.class);

        submission = submissionJpaService.register(new Submission(), clientDto);
        submissionDto = modelMapper.map(submission, SubmissionDto.class);


        File csvFile = new File("src/test/resources/detected.csv");
        FileInputStream input3 = new FileInputStream(csvFile);

        File textFile = new File("src/test/resources/classes.txt");
        FileInputStream input4 = new FileInputStream(textFile);

        multipartFileCsv = new MockMultipartFile(
                "detected.csv", "detected.csv",
                "application/octet-stream", input3.readAllBytes());


        multipartFileClasses = new MockMultipartFile(
                "classes.txt", "classes.txt",
                "text/plain", input4.readAllBytes()
        );
        input3.close();
        input4.close();
    }

    /**
     * Register a submission.
     */
    @Test
    public void registerSubmissionClientTest() {

        // assert
        assertThat(submissionJpaService.count()).isEqualTo(1);

        submission = submissionJpaService.findById(submission.getId());
        client = basicUserJpaService.findById(client.getId());
        assertThat(submission.getClient()).isNotNull();
        assertThat(client.getSubmission()).isNotNull();
    }

    /**
     * Register an existing submission.
     */
    @Test
    public void registerExistingSubmissionTest() {

        // act and assert
        assertThrows(ExistsException.class, () ->
                submissionJpaService.register(submission, clientDto));
    }

    /**
     * Admins cannot submit a submission.
     */
    @Test
    public void registerAdminCannotSubmitTest() {
        // act and assert
        assertThrows(AuthorityException.class, () ->
                submissionJpaService.register(new Submission(), adminDto));
    }

    /**
     * Non Existent client cannot submit a test.
     */
    @Test
    public void registerNonExistentClientCannotSubmitTest() {
        // act and assert
        assertThrows(NotFoundException.class, () ->
                submissionJpaService.register(new Submission(), new UserDto()));
    }

    /**
     * Assign container test.
     *
     * @throws NotFoundException NotFoundException
     */
    @Test
    public void assignContainerTest() throws NotFoundException {

        // act
        submissionJpaService.assignContainer(submission, containerDto);

        submission = submissionJpaService.findById(submission.getId());
        container = containerJpaService.findById(container.getId());

        // assert
        assertThat(submission.getContainer()).isEqualTo(container);
        assertThat(container.getSubmission()).isEqualTo(submission);
    }

    /**
     * Assign non existing container test.
     */
    @Test
    public void assignNonExistingContainerTest() {

        // act and assert
        assertThrows(NotFoundException.class, () -> {
            submissionJpaService.assignContainer(new Submission(), new ContainerDto());
        });
    }

    /**
     * Update is not supported.
     */
    @Test
    public void updateNotSupportedTest() {

        // act and assert
        assertThrows(UnsupportedOperationException.class, () -> {
            submissionJpaService.update(submissionDto, new SubmissionDto());
        });
    }

    /**
     * Delete a submission.
     *
     * @throws NotFoundException NotFoundException
     */
    @Test
    public void deleteSubmissionTest() throws Exception {

        // arrange
        assertThat(projectJpaService.count()).isEqualTo(1);
        Container container = containerJpaService
                .fillContainer(multipartFileCsv, multipartFileClasses,
                        new Container(42.0, "test", "testContainer"),
                        projectDto);
        submissionJpaService.assignContainer(submission, new ContainerDto(container));

        // act
        submissionJpaService.delete(submissionDto);

        // assert
        assertThat(projectHolderJpaService.count()).isEqualTo(1);
        assertThat(projectJpaService.count()).isEqualTo(1);
        assertThat(containerJpaService.count()).isEqualTo(2);
        assertThat(csvJpaService.count()).isEqualTo(2);
        assertThat(basicUserJpaService.count()).isEqualTo(1);
        assertThat(adminJpaService.count()).isEqualTo(1);
        assertThat(submissionJpaService.count()).isEqualTo(0);
    }

    /**
     * Delete a submission.
     *
     * @throws ExistsException   ExistsException
     * @throws NotFoundException NotFoundException
     */
    @Test
    public void deleteSimpleSubmissionTest()
            throws ExistsException, NotFoundException {

        // arrange
        Submission submission2 = new Submission(container, null);
        submissionJpaService.register(submission2);
        SubmissionDto submission2Dto = modelMapper.map(submission2,
                SubmissionDto.class);

        // act
        submissionJpaService.delete(submission2Dto);

        // assert
        assertThat(containerJpaService.count()).isEqualTo(1);
        assertThat(submissionJpaService.count()).isEqualTo(1);

        // act
        submissionJpaService.delete(submissionDto);

        // assert
        assertThat(containerJpaService.count()).isEqualTo(1);
        assertThat(submissionJpaService.count()).isEqualTo(0);
    }

    /**
     * Delete container.
     *
     * @throws NotFoundException NotFoundException
     */
    @Test
    public void deleteContainerWithSubmissionTest() throws NotFoundException {

        // arrange
        submissionJpaService.assignContainer(submission, containerDto);

        // act
        containerJpaService.delete(containerDto);

        // assert
        assertThat(projectHolderJpaService.count()).isEqualTo(1);
        assertThat(projectJpaService.count()).isEqualTo(1);
        assertThat(containerJpaService.count()).isEqualTo(0);
        assertThat(csvJpaService.count()).isEqualTo(0);
        assertThat(basicUserJpaService.count()).isEqualTo(1);
        assertThat(adminJpaService.count()).isEqualTo(1);
        assertThat(submissionJpaService.count()).isEqualTo(0);
    }

    /**
     * Delete client.
     *
     * @throws NotFoundException NotFoundException
     */
    @Test
    public void deleteClientDeleteSubmissionTrickleDownTest() throws NotFoundException {

        // act
        adminJpaService.delete(clientDto);

        // assert
        assertThat(projectHolderJpaService.count()).isEqualTo(0);
        assertThat(projectJpaService.count()).isEqualTo(0);
        assertThat(containerJpaService.count()).isEqualTo(0);
        assertThat(csvJpaService.count()).isEqualTo(0);
        assertThat(basicUserJpaService.count()).isEqualTo(0);
        assertThat(adminJpaService.count()).isEqualTo(1);
        assertThat(submissionJpaService.count()).isEqualTo(0);
    }

    /**
     * register submission test.
     *
     * @throws ExistsException    ExistsException
     * @throws NotFoundException  NotFoundException
     * @throws IOException        IOException
     * @throws AuthorityException AuthorityException
     */
    @Test
    public void registerSubmissionTest()
            throws ExistsException, NotFoundException, IOException, AuthorityException {

        // arrange
        submissionJpaService.delete(submissionDto);
        Record record1 = Record.builder().frameNum(2)
                .objectId(1).trackerL(3).label("testLabel200")
                .trackerH(3).trackerW(5).trackerT(4).build();

        RecordDto recordDto1 = modelMapper.map(record1, RecordDto.class);

        Record record2 = Record.builder().frameNum(2)
                .objectId(4).trackerL(6).label("testLabel400")
                .trackerH(3).trackerW(7).trackerT(5).build();

        RecordDto recordDto2 = modelMapper.map(record2, RecordDto.class);

        List<RecordDto> recordDtoList = Arrays.asList(recordDto1, recordDto2);

        RecordListDto recordListDto = new RecordListDto(recordDtoList, container.getId());

        // act
        submissionJpaService.register(recordListDto, client.getId());
        submissionJpaService.register(recordListDto, client.getId());
        submissionJpaService.register(recordListDto, client.getId());

        // assert
        assertThat(submissionJpaService.count()).isEqualTo(1);
        assertThat(containerJpaService.count()).isEqualTo(1);

        // act
        containerJpaService.delete(containerDto);

        // assert
        assertThat(containerJpaService.count()).isEqualTo(0);
        assertThat(submissionJpaService.count()).isEqualTo(0);
    }


}
