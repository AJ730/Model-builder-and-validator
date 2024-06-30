package nl.tudelft.sp.modelchecker.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javassist.NotFoundException;
import nl.tudelft.sp.modelchecker.Application;
import nl.tudelft.sp.modelchecker.SecurityFilter.MockSpringSecurityFilter;
import nl.tudelft.sp.modelchecker.cloud.Connection;
import nl.tudelft.sp.modelchecker.dto.ContainerDto;
import nl.tudelft.sp.modelchecker.dto.ProjectDto;
import nl.tudelft.sp.modelchecker.entities.Container;
import nl.tudelft.sp.modelchecker.entities.Project;
import nl.tudelft.sp.modelchecker.exceptions.ExistsException;
import nl.tudelft.sp.modelchecker.services.*;
import nl.tudelft.sp.modelchecker.services.servicebeans.AzureBlobJpaService;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Percentage;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = Application.class)
@Transactional
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class ContainerControllerTest {

    final String authority = "APPROLE_ADMIN";
    private final String create = "/api/create/container";
    private final String update = "/api/update/container";
    private final String delete = "/api/delete/container";
    private final String list = "/api/list/container";
    private final String get = "/api/get/container";
    private final String classes = "/api/get/classes";
    @Autowired
    WebApplicationContext context;
    MockMvc mvc;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ProjectService projectService;
    @Autowired
    ContainerService containerService;
    @Autowired
    CsvService csvService;
    @Autowired
    Connection connection;
    @Autowired
    PersistentCsvService persistentCsvService;
    @Autowired
    PersistentRecordService persistentRecordService;
    @Autowired
    RecordService recordService;
    @Autowired
    ContainerController containerController;
    @Autowired
    AzureBlobJpaService azureBlobJpaService;
    @Autowired
    ObjectWriter objectWriter;
    @Autowired
    ObjectMapper objectMapper;
    private Container container;
    private ContainerDto containerDto;
    private Long containerId;
    private String containerJson;

    private ContainerDto containerProjectDto;
    private Long containerProjectId;
    private String containerProjectJson;

    private Project project;
    private ProjectDto projectDto;
    private Long projectId;

    private MockMultipartFile csvMultipartFile;
    private MockMultipartFile videoMultiPartFile;
    private MockMultipartFile classMultiPartFile;

    /**
     * Before Each.
     *
     * @throws JsonProcessingException JsonProcessingException
     */
    @BeforeEach
    void setUp() throws IOException, ExistsException, NotFoundException {

        // arrange
        project = projectService.register(new Project("title", "desc"));
        projectDto = modelMapper.map(project, ProjectDto.class);
        projectId = project.getId();


        Container newContainer = containerService.register(new Container(23.0,
                "tesc", "desc"), projectDto);
        containerProjectDto = new ContainerDto(newContainer);
        containerProjectId = newContainer.getId();
        containerProjectJson = objectWriter.writeValueAsString(containerProjectDto);


        FileInputStream videoFile = new FileInputStream(new File("src/test/resources/output.mp4"));
        FileInputStream csvFile = new FileInputStream(new File("src/test/resources/detected.csv"));
        FileInputStream classFile = new FileInputStream(new File("src/test/resources/classes.txt"));


        videoMultiPartFile = new MockMultipartFile("video", "output.mp4",
                "mp4", videoFile);
        csvMultipartFile =
                new MockMultipartFile("csv", "detected.csv",
                        "application/octet-stream", csvFile);
        classMultiPartFile =
                new MockMultipartFile("classes", "classes.txt",
                        "text/plain", classFile);

        container = containerService.register(new Container(23.0,
                "tesc", "desc"));
        containerService.registerClasses(classMultiPartFile, new ContainerDto(container.getId()));
        container = containerService.findById(container.getId());
        containerDto = new ContainerDto(container);
        containerId = container.getId();
        containerJson = objectWriter.writeValueAsString(containerDto);
    }

    /**
     * create Container.
     *
     * @throws Exception Exception.
     */
    @Test
    @WithMockUser(authorities = authority)
    void createContainer() throws Exception {

        String blobName = "output.mp4";
        // arrange
        getAuthentication(true);
        // act and assert
        mvc.perform(MockMvcRequestBuilders.multipart(create)
                .file(csvMultipartFile)
                .file(classMultiPartFile)
                .param("blobName", blobName)
                .param("projectId", projectId.toString())
                .param("description", "desc").param("name", "test"))
                .andExpect(status().isOk());

        List<Container> containers = containerService.findAll();

        assertThat(containerService
                .findById(containers.get(2).getId()).getFrameRate()).isCloseTo(29.97,
                Percentage.withPercentage(0.01));
        assertThat(projectService.count()).isEqualTo(1);

        assertThat(containerService.count()).isEqualTo(3);
        assertThat(csvService.count()).isEqualTo(1);

        assertThat(persistentCsvService.count()).isEqualTo(1);
        assertThat(persistentRecordService.count()).isEqualTo(451);

        assertThat(recordService.count()).isEqualTo(451);
    }

    /**
     * Null container.
     *
     * @throws Exception Exception
     */
    @Test
    void getContainerNullTest() throws Exception {
        // arrange
        getAuthentication(false);

        ContainerDto requestDto = new ContainerDto(100L);
        String request = objectWriter.writeValueAsString(requestDto);

        // act and assert
        mvc.perform(post(get)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertThat(result.getResolvedException())
                        .isInstanceOf(NotFoundException.class));
    }


    /**
     * create Container without authorization.
     *
     * @throws Exception Exception.
     */
    @Test
    void createWithoutAuthorization() throws Exception {

        // arrange
        getAuthentication(true);

        // act and assert
        mvc.perform(MockMvcRequestBuilders.multipart(create)
                .file(videoMultiPartFile)
                .file(csvMultipartFile)
                .file(classMultiPartFile)
                .param("projectId", "1").param("frameRate", "22.3")
                .param("description", "desc").param("name", "test"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * update container.
     *
     * @throws Exception Exception
     */
    @Test
    @WithMockUser(authorities = authority)
    void updateContainer() throws Exception {

        // arrange
        getAuthentication(true);

        Container updatedContainer = new Container(54.0,
                "tesc", "dess");
        updatedContainer.setId(containerId);

        String request = objectWriter.writeValueAsString(new ContainerDto(updatedContainer));

        // act and assert
        mvc.perform(post(update)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isOk());

        assertThat(containerService.findById(updatedContainer.getId()))
                .isEqualToComparingOnlyGivenFields(updatedContainer,
                        "frameRate", "description", "name");
    }

    /**
     * update container without authorization.
     *
     * @throws Exception Exception
     */
    @Test
    void updateWithoutAuthorization() throws Exception {

        // arrange
        getAuthentication(true);
        String request = objectWriter.writeValueAsString(containerDto);

        // act and assert
        mvc.perform(post(update)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isUnauthorized());
    }


    /**
     * get proxy of a container without authorization.
     *
     * @throws Exception Exception
     */
    @Test
    void getContainer() throws Exception {

        // arrange
        getAuthentication(false);

        ContainerDto requestDto = new ContainerDto(containerId);
        String request = objectWriter.writeValueAsString(requestDto);

        // act and assert
        mvc.perform(post(get)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(content().json(containerJson))
                .andExpect(status().isOk());
    }

    /**
     * delete container.
     *
     * @throws Exception Exception
     */
    @WithMockUser(authorities = authority)
    @Test
    void deleteContainer() throws Exception {

        // arrange
        getAuthentication(true);

        ContainerDto requestDto = new ContainerDto(containerProjectId);
        String request = objectWriter.writeValueAsString(requestDto);

        // act and assert
        mvc.perform(post(delete)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isOk());

        assertThat(containerService.findById(containerProjectId)).isNull();
        assertThat(containerService.count()).isEqualTo(1);
    }

    /**
     * delete container without authorization.
     *
     * @throws Exception Exception
     */
    @Test
    void deleteWithoutAuthorization() throws Exception {

        // arrange
        getAuthentication(true);

        ContainerDto requestDto = new ContainerDto(containerProjectId);
        String request = objectWriter.writeValueAsString(requestDto);

        // act and assert
        mvc.perform(post(delete)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isUnauthorized());
    }

    /**
     * list containers.
     *
     * @throws Exception Exception
     */
    @WithMockUser(authorities = authority)
    @Test
    void getContainers() throws Exception {

        // arrange
        getAuthentication(true);

        List<ContainerDto> containerList = new ArrayList<>();
        containerList.add(containerDto);
        containerList.add(containerProjectDto);

        containerJson = objectWriter.writeValueAsString(containerList);

        // act and assert
        mvc.perform(post(list))
                .andExpect(content().json(containerJson));
    }

    /**
     * list containers without permission.
     *
     * @throws Exception Exception
     */
    @Test
    void getContainersWithoutAuthorization() throws Exception {

        // arrange
        getAuthentication(true);

        // act and assert
        mvc.perform(post(list))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Get classes.
     *
     * @throws Exception Exception
     */
    @Test
    @WithMockUser(authorities = authority)
    public void getClasses() throws Exception {

        // arrange
        getAuthentication(true);
        List<String> clazz = container.getClasses();
        String expected = objectWriter.writeValueAsString(clazz);

        ContainerDto containerDto = new ContainerDto(containerId);
        containerJson = objectWriter.writeValueAsString(containerDto);

        // act and assert
        mvc.perform(post(classes)
                .contentType(MediaType.APPLICATION_JSON)
                .content(containerJson))
                .andExpect(content().json(expected));
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

}