package nl.tudelft.sp.modelchecker.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Scanner;
import javassist.NotFoundException;
import nl.tudelft.sp.modelchecker.Application;
import nl.tudelft.sp.modelchecker.SecurityFilter.MockSpringSecurityFilter;
import nl.tudelft.sp.modelchecker.dto.*;
import nl.tudelft.sp.modelchecker.entities.*;
import nl.tudelft.sp.modelchecker.exceptions.AuthorityException;
import nl.tudelft.sp.modelchecker.exceptions.DateException;
import nl.tudelft.sp.modelchecker.exceptions.ExistsException;
import nl.tudelft.sp.modelchecker.services.ProjectService;
import nl.tudelft.sp.modelchecker.services.servicebeans.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = Application.class)
@Transactional
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class SubmissionControllerTest {

    final String create = "/api/create/submission";
    final String delete = "/api/delete/submission";

    @Autowired
    WebApplicationContext context;
    MockMvc mvc;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    ObjectWriter objectWriter;
    @Autowired
    ProjectService projectService;
    @Autowired
    SubmissionJpaService submissionJpaService;
    @Autowired
    ContainerJpaService containerJpaService;
    @Autowired
    RecordJpaService recordJpaService;
    @Autowired
    BasicUserJpaService basicUserJpaService;
    @Autowired
    AdminJpaService adminJpaService;
    @Autowired
    JwtTokenJpaService jwtTokenJpaService;
    @Autowired
    CsvJpaService csvJpaService;
    @Autowired
    ProjectHolderJpaService projectHolderJpaService;
    @Autowired
    PersistentCsvJpaService persistentCsvJpaService;
    @Autowired
    PersistentRecordJpaService persistentRecordJpaService;

    ProjectHolder projectHolder;
    ProjectHolderDto projectHolderDto;

    Project project;
    ProjectDto projectDto;

    Submission submission;
    SubmissionDto submissionDto;

    Container container;
    ContainerDto containerDto;


    Csv csv;
    CsvDto csvDto;

    Admin admin;
    UserDto adminDto;
    BasicUser basicUser;
    UserDto basicUserDto;
    RecordListDto recordListDto;

    MockMultipartFile csvMultipartFile2;
    MockMultipartFile videoMultiPartFile2;
    MockMultipartFile classMultiPartFile;

    String adminToken;
    String userToken;
    String submissionJson;
    String recordListDtoJson;

    @BeforeEach
    void setUp()
            throws Exception {

        // arrange
        userSetUp();
        createProject();
        containerSetUp();

        submission = submissionJpaService.register(new Submission(), basicUserDto);
        submission = submissionJpaService
                .assignContainer(submission, new ContainerDto(container.getId()));
        submissionDto = new SubmissionDto(submission.getId());
        submissionJson = objectWriter.writeValueAsString(submissionDto);

    }

    /**
     * create submission.
     *
     * @throws Exception Exception
     */
    @Test
    void createTest() throws Exception {

        // arrange
        getAuthentication(false);

        assertThat(recordJpaService.count()).isEqualTo(399);
        assertThat(persistentRecordJpaService.count()).isEqualTo(399);


        assertThat(csvJpaService.count()).isEqualTo(1);
        assertThat(persistentCsvJpaService.count()).isEqualTo(1);

        assertThat(containerJpaService.count()).isEqualTo(1);
        assertThat(submissionJpaService.count()).isEqualTo(1);
        assertThat(projectService.count()).isEqualTo(1);
        assertThat(projectHolderJpaService.count()).isEqualTo(1);

        List<RecordDto> recordDtos = csvJpaService.getRecordsInCsv(csvDto);

        Long recordId0 = recordDtos.get(0).getId();
        Long recordId1 = recordDtos.get(1).getId();

        assertThat(recordJpaService.findById(recordId0).getLabel()).isEqualTo("paper");
        assertThat(recordJpaService.findById(recordId1).getLabel()).isEqualTo("residual");

        recordDtos.get(0).setLabel("pp");
        recordDtos.get(1).setLabel("pp");

        recordListDto = new RecordListDto(recordDtos, container.getId());
        recordListDtoJson = objectWriter.writeValueAsString(recordListDto);

        // act
        mvc.perform(post(create)
                .header("oid", basicUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(recordListDtoJson))
                .andExpect(status().isOk());

        // assert
        assertThat(recordJpaService.findById(recordId0).getLabel()).isEqualTo("pp");
        assertThat(recordJpaService.findById(recordId1).getLabel()).isEqualTo("pp");

        assertThat(submissionJpaService.count()).isEqualTo(1);

        assertThat(recordJpaService.count()).isEqualTo(399);
        assertThat(persistentRecordJpaService.count()).isEqualTo(399);

        assertThat(csvJpaService.count()).isEqualTo(1);
        assertThat(persistentCsvJpaService.count()).isEqualTo(1);

        assertThat(containerJpaService.count()).isEqualTo(1);

    }

    /**
     * create submission with new records.
     */
    @Test
    void createSubmissionWithNewRecordsTest() throws Exception {

        // arrange
        getAuthentication(false);

        List<RecordDto> recordDtos = csvJpaService.getRecordsInCsv(csvDto);
        recordListDto = new RecordListDto(recordDtos, container.getId());
        recordListDtoJson = objectWriter.writeValueAsString(recordListDto);

        mvc.perform(post(create)
                .header("oid", basicUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(recordListDtoJson))
                .andExpect(status().isOk());

        recordDtos.subList(0, 10).clear();
        recordDtos.forEach(dto -> dto.setId(null));

        recordListDtoJson = objectWriter.writeValueAsString(recordListDto);

        // act and assert
        mvc.perform(post(create)
                .header("oid", basicUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(recordListDtoJson))
                .andExpect(status().isOk());

        assertThat(recordJpaService.count()).isEqualTo(399);
        assertThat(persistentRecordJpaService.count()).isEqualTo(399);
        assertThat(submissionJpaService.count()).isEqualTo(1);

    }

    /**
     * create submission without authorization.
     *
     * @throws Exception Exception
     */
    @Test
    void createWithoutAuthorization() throws Exception {

        // arrange
        getAuthentication(true);
        List<RecordDto> recordDtos = csvJpaService.getRecordsInCsv(csvDto);
        recordListDto = new RecordListDto(recordDtos, container.getId());
        recordListDtoJson = objectWriter.writeValueAsString(recordListDto);

        // act and assert
        mvc.perform(post(create)
                .header("oid", basicUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(recordListDtoJson))
                .andExpect(status().isUnauthorized());
    }

    /**
     * test admin create submission.
     *
     * @throws Exception Exception
     */
    @Test
    void adminCreateSubmissionTest() throws Exception {
        // arrange
        getAuthentication(false);
        List<RecordDto> recordDtos = csvJpaService.getRecordsInCsv(csvDto);
        recordListDto = new RecordListDto(recordDtos, container.getId());
        recordListDtoJson = objectWriter.writeValueAsString(recordListDto);

        // act and assert
        mvc.perform(post(create)
                .header("oid", admin.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(recordListDtoJson))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(AuthorityException.class));
    }

    /**
     * delete submission.
     *
     * @throws Exception Exception
     */
    @Test
    void deleteTest() throws Exception {

        // arrange
        getAuthentication(false);

        // act and assert
        mvc.perform(post(delete)
                .contentType(MediaType.APPLICATION_JSON)
                .content(submissionJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(submission.getId()));

        assertThat(submissionJpaService.count()).isEqualTo(0);
        assertThat(csvJpaService.count()).isEqualTo(1);
        assertThat(containerJpaService.count()).isEqualTo(1);
    }

    /**
     * delete submission without authorization.
     *
     * @throws Exception Exception
     */
    @Test
    void deleteWithoutAuthorization() throws Exception {

        // arrange
        getAuthentication(true);

        // act and assert
        mvc.perform(post(delete).contentType(MediaType.APPLICATION_JSON).content(submissionJson))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Get Authentication context.
     */
    private void getAuthentication(boolean admin) {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(admin ? springSecurity() : springSecurity(new MockSpringSecurityFilter()))
                .alwaysDo(print())
                .build();
    }

    /**
     * user setup.
     *
     * @throws FileNotFoundException FileNotFoundException
     * @throws ExistsException       ExistsException
     * @throws DateException         DateException
     * @throws NotFoundException     NotFoundException
     * @throws AuthorityException    AuthorityException
     */
    private void userSetUp()
            throws FileNotFoundException, ExistsException, DateException, NotFoundException,
            AuthorityException {
        File file = new File("src/test/resources/Tokens.txt");
        FileReader fr = new FileReader(file);
        Scanner sc = new Scanner(fr);
        adminToken = sc.nextLine();
        userToken = sc.nextLine();
        sc.close();
        User user = jwtTokenJpaService.parseToken(adminToken);
        admin = adminJpaService.findById(user.getId());
        adminDto = new UserDto(admin);
        user = jwtTokenJpaService.parseToken(userToken);
        basicUser = basicUserJpaService.findById(user.getId());
        basicUserDto = new UserDto(user);
    }

    /**
     * project setup.
     *
     * @throws ExistsException   ExistsException
     * @throws NotFoundException NotFoundException
     */
    private void createProject() throws ExistsException, NotFoundException {
        projectHolder = basicUser.getProjectHolder();
        projectHolderDto = new ProjectHolderDto(projectHolder.getId());

        project = projectService
                .register(new Project("title", "desc"),
                        projectHolderDto, adminDto);
        projectDto = new ProjectDto(project);
    }

    /**
     * Container setup.
     *
     * @throws Exception Exception
     */
    private void containerSetUp() throws Exception {
        FileInputStream classFile =
                new FileInputStream(new File("src/test/resources/classes.txt"));
        classMultiPartFile =
                new MockMultipartFile("classes", "classes.txt",
                        "text/plain", classFile);
        container = containerJpaService.register(new Container(23.0,
                "tesc", "desc"), projectDto);
        containerJpaService
                .registerClasses(classMultiPartFile, new ContainerDto(container.getId()));
        container = containerJpaService.findById(container.getId());
        containerDto = new ContainerDto(container);


        FileInputStream csvFile2 =
                new FileInputStream(new File("src/test/resources/detected2.csv"));
        csvMultipartFile2 = new MockMultipartFile(
                "csv2", "detected2.csv",
                "application/octet-stream", csvFile2);

        csv = csvJpaService.register(new Csv(), containerDto);
        csvDto = new CsvDto(csv);
        recordJpaService.save(csvMultipartFile2, csvDto);
        persistentCsvJpaService.saveCsv(containerDto, csvDto);

    }
}