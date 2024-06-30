package nl.tudelft.sp.modelchecker.database;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.util.List;
import javassist.NotFoundException;
import nl.tudelft.sp.modelchecker.Application;
import nl.tudelft.sp.modelchecker.dto.ContainerDto;
import nl.tudelft.sp.modelchecker.dto.ProjectDto;
import nl.tudelft.sp.modelchecker.dto.ProjectHolderDto;
import nl.tudelft.sp.modelchecker.dto.UserDto;
import nl.tudelft.sp.modelchecker.entities.*;
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
public class DatabaseProjectTest {

    @Autowired
    ProjectHolderJpaService projectHolderJpaService;

    @Autowired
    ProjectJpaService projectJpaService;

    @Autowired
    ContainerJpaService containerJpaService;

    @Autowired
    CsvJpaService csvJpaService;

    @Autowired
    AdminJpaService adminJpaService;

    @Autowired
    BasicUserJpaService basicUserJpaService;

    @Autowired
    PersistentRecordJpaService persistentRecordJpaService;

    @Autowired
    PersistentCsvJpaService persistentCsvJpaService;

    @Autowired
    SubmissionJpaService submissionJpaService;

    @Autowired
    ModelMapper modelMapper;

    private Admin admin;
    private UserDto adminDto;

    private BasicUser basicUser;
    private UserDto basicUserDto;

    private Container container;

    private ContainerDto containerDto;

    private Project project;
    private ProjectDto projectDto;

    private ProjectHolder projectHolder;
    private ProjectHolderDto projectHolderDto;

    private Submission submission;
    private Csv csv;


    private String desc = "description";
    private String title = "title";
    private String test = "test";

    /**
     * Initialize a test environment.
     *
     * @throws ExistsException                          ExistsException
     * @throws NotFoundException                        NotFoundException
     * @throws IOException                              IOException
     * @throws SQLIntegrityConstraintViolationException SQLIntegrityConstraintViolationException
     */
    @BeforeEach
    public void setUp() throws ExistsException,
            NotFoundException, IOException,
            SQLIntegrityConstraintViolationException, DateException, AuthorityException {

        // arrange
        admin = new Admin("1A", "akash", "amalan_akash@hotmail.com",
                Date.valueOf(LocalDate.now()));

        admin = adminJpaService.registerUser(admin);
        adminDto = modelMapper.map(admin, UserDto.class);

        basicUser = new BasicUser("4A", "jake", "jake@gmail.com",
                Date.valueOf(LocalDate.now()));
        basicUser = basicUserJpaService.registerUser(basicUser);
        basicUserDto = modelMapper.map(basicUser, UserDto.class);

        projectHolder = projectHolderJpaService.register(new ProjectHolder());
        projectHolderDto = modelMapper.map(projectHolder, ProjectHolderDto.class);

        project = projectJpaService.register(new Project(title, desc),
                projectHolderDto, adminDto);
        projectDto = modelMapper.map(project, ProjectDto.class);

        container = containerJpaService.register(
                new Container(42.0, test, "testContainer"), projectDto);
        containerDto = modelMapper.map(container, ContainerDto.class);

        submission = submissionJpaService.register(new Submission(), basicUserDto);
        submissionJpaService.assignContainer(submission, containerDto);

        csv = csvJpaService.register(new Csv(), containerDto);
    }

    /**
     * Register a project.
     */
    @Test
    public void registerProjectTest() {

        // assert
        project = projectJpaService.findById(project.getId());
        assertThat(projectHolder.getProjects().toArray()[0]).isEqualTo(project);
        assertThat(project.getProjectHolder()).isEqualTo(projectHolder);

        assertThat(projectJpaService.count()).isEqualTo(1);
    }

    /**
     * Register existing project.
     */
    @Test
    public void registerExistingProjectTest() {

        // act and assert
        assertThrows(ExistsException.class, () -> {
            projectJpaService.register(project, projectHolderDto, adminDto);
        });
    }

    /**
     * Register projectHolder.
     */
    @Test
    public void registerProjectHolderNonExistentTest() {

        // act and assert
        assertThrows(ExistsException.class, () -> {
            projectJpaService.register(project, new ProjectHolderDto(), adminDto);
        });
    }

    /**
     * Update projectHolder.
     *
     * @throws NotFoundException NotFoundException
     */
    @Test
    public void updateProjectHolderTest() throws NotFoundException {

        // act
        Project project1 = projectJpaService.update(projectDto, new ProjectDto(title, test));

        // assert
        assertThat(projectJpaService.findById(projectDto.getId()))
                .isEqualToComparingOnlyGivenFields(project1, title, desc);
    }

    /**
     * Delete project.
     *
     * @throws NotFoundException NotFoundException
     */
    @Test
    public void deleteProjectsTrickleDownTest() throws NotFoundException {

        // act
        projectJpaService.delete(projectDto);

        // assert
        assertThat(projectJpaService.count()).isEqualTo(0);
        assertThat(csvJpaService.count()).isEqualTo(0);
        assertThat(containerJpaService.count()).isEqualTo(0);
        assertThat(submissionJpaService.count()).isEqualTo(0);

    }

    /**
     * Delete project with multiple containers.
     *
     * @throws ExistsException                          ExistsException
     * @throws NotFoundException                        NotFoundException
     * @throws SQLIntegrityConstraintViolationException SQLIntegrityConstraintViolationException
     */
    @Test
    public void deleteProjectsMultipleContainersTest() throws Exception {

        // arrange
        File videoFile = new File("src/test/resources/output.mp4");
        FileInputStream input1 = new FileInputStream(videoFile);

        File csvFile = new File("src/test/resources/detected.csv");
        FileInputStream input2 = new FileInputStream(csvFile);

        File textFile = new File("src/test/resources/classes.txt");
        FileInputStream input3 = new FileInputStream(textFile);


        MultipartFile multipartFileCsv = new MockMultipartFile(
                "detected.csv", "detected.csv",
                "application/octet-stream", input2.readAllBytes());

        MultipartFile multipartFileVideo = new MockMultipartFile(
                "output.mp4", "output.mp4",
                "mp4", input1.readAllBytes()
        );

        MultipartFile multipartFileClasses = new MockMultipartFile(
                "classes.txt", "classes.txt",
                "text/plain", input3.readAllBytes()
        );

        containerJpaService.fillContainer(multipartFileCsv,
                multipartFileClasses,
                new Container(42.0, test, "test2Container"), projectDto);

        assertThat(containerJpaService.count()).isEqualTo(2);
        assertThat(csvJpaService.count()).isEqualTo(2);


        // act
        projectJpaService.delete(projectDto);

        // assert
        assertThat(containerJpaService.count()).isEqualTo(0);
        assertThat(csvJpaService.count()).isEqualTo(0);
        assertThat(persistentRecordJpaService.count()).isEqualTo(0);
        assertThat(persistentCsvJpaService.count()).isEqualTo(0);
        assertThat(submissionJpaService.count()).isEqualTo(0);
    }


    /**
     * Register test whether admin is null.
     */
    @Test
    public void registerAdminNullTest() {

        // act and assert
        assertThrows(NotFoundException.class, () -> {
            projectJpaService.register(new Project(), projectHolderDto, new UserDto("34A"));
        });
    }

    /**
     * ProjectHolder not found when registering.
     */
    @Test
    public void registerNotFoundExceptionTest() {

        // act and assert
        assertThrows(NotFoundException.class, () -> {
            projectJpaService.register(new Project(), new ProjectHolderDto(), adminDto);
        });
    }

    /**
     * Project not found when updating.
     */
    @Test
    public void updateNotFoundExceptionTest() {

        // act and assert
        assertThrows(NotFoundException.class, () -> {
            projectJpaService.update(new ProjectDto(), projectDto);
        });
    }

    /**
     * Update with all null fields no change.
     *
     * @throws NotFoundException NotFoundException
     */
    @Test
    public void updateNoTitleTest() throws NotFoundException {

        // act
        projectJpaService.update(projectDto, new ProjectDto(null, null));

        // assert
        assertThat(projectJpaService.findById(projectDto.getId()))
                .isEqualToComparingOnlyGivenFields(projectDto, title,
                        desc);
    }


    /**
     * Delete project does not delete project holder.
     *
     * @throws NotFoundException NotFoundException
     */
    @Test
    public void deleteProjectDoesNotDeleteProjectHolderTest() throws NotFoundException {

        // act
        projectJpaService.delete(projectDto);

        // assert
        assertThat(projectJpaService.findById(project.getId())).isNull();
        assertThat(projectHolderJpaService.findById(projectDto.getProjectHolderId())).isNotNull();
        assertThat(projectJpaService.count()).isEqualTo(0);
        assertThat(projectHolderJpaService.count()).isEqualTo(1);
        assertThat(submissionJpaService.count()).isEqualTo(0);
    }

    /**
     * Get container Dtos in project.
     */
    @Test
    public void getContainerDtosInProjectTest() throws NotFoundException,
            ExistsException, SQLIntegrityConstraintViolationException {

        // arrange
        Container container1 = containerJpaService.register(
                new Container(34.0, test, "testContainer"), projectDto);
        ContainerDto containerDto1 = modelMapper.map(container1, ContainerDto.class);

        // act
        List<ContainerDto> containerList = projectJpaService.getContainerDtosInProject(projectDto);

        // assert
        assertThat(containerList.size()).isEqualTo(2);
    }

    /**
     * Get container Dtos in a project that doesn't exist.
     */
    @Test
    public void getContainerDtosInNullProjectTest() {

        // act and assert
        assertThrows(NotFoundException.class, () -> {
            projectJpaService.getContainerDtosInProject(new ProjectDto(11L));
        });
    }

    /**
     * Change Project holder.
     */
    @Test
    public void changeProjectHolderTest() throws ExistsException, NotFoundException {

        // arrange
        ProjectHolder projectHolder2 =
                projectHolderJpaService.register(new ProjectHolder());
        ProjectHolderDto projectHolderDto2 =
                modelMapper.map(projectHolder2, ProjectHolderDto.class);

        projectJpaService.register(new Project(title, desc),
                projectHolderDto2, adminDto);
        projectDto.setProjectHolderId(projectHolder2.getId());

        // act
        projectJpaService.changeProjectHolder(projectDto);

        project = projectJpaService.findById(project.getId());
        projectHolder2 = projectHolderJpaService.findById(projectHolder2.getId());
        projectHolder = projectHolderJpaService.findById(projectHolder.getId());

        // assert
        assertThat(project.getProjectHolder()).isEqualTo(projectHolder2);
        assertThat(projectHolder2.getProjects().size()).isEqualTo(2);
        assertThat(projectHolder.getProjects().size()).isEqualTo(0);
    }

    /**
     * Change Project Holder null test.
     */
    @Test
    public void changeProjectHolderNullTest() throws ExistsException {

        // arrange
        Project project2 = projectJpaService.register(
                new Project("like", "sike"));

        ProjectDto projectDto2 = modelMapper.map(project2, ProjectDto.class);

        // act and assert
        assertThrows(NotFoundException.class, () -> {
            projectJpaService.changeProjectHolder(projectDto2);
        });
    }

    /**
     * Target Project Holder not found.
     */
    @Test
    public void changeProjectHolderTargetHolderNullTest() {
        // arrange
        projectDto.setProjectHolderId(10L);

        // act and assert
        assertThrows(NotFoundException.class, () -> {
            projectJpaService.changeProjectHolder(projectDto);
        });
    }
}
