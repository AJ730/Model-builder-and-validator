package nl.tudelft.sp.modelchecker.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Scanner;
import javassist.NotFoundException;
import nl.tudelft.sp.modelchecker.Application;
import nl.tudelft.sp.modelchecker.SecurityFilter.MockSpringSecurityFilter;
import nl.tudelft.sp.modelchecker.dto.ContainerDto;
import nl.tudelft.sp.modelchecker.dto.ProjectDto;
import nl.tudelft.sp.modelchecker.dto.ProjectHolderDto;
import nl.tudelft.sp.modelchecker.dto.UserDto;
import nl.tudelft.sp.modelchecker.entities.*;
import nl.tudelft.sp.modelchecker.exceptions.AuthorityException;
import nl.tudelft.sp.modelchecker.exceptions.DateException;
import nl.tudelft.sp.modelchecker.exceptions.ExistsException;
import nl.tudelft.sp.modelchecker.repositories.ProjectRepository;
import nl.tudelft.sp.modelchecker.services.servicebeans.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@Transactional
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class ProjectControllerTest {

    final String authority = "APPROLE_ADMIN";
    final String get = "/api/get/project";
    final String create = "/api/create/project";
    final String update = "/api/update/project";
    final String delete = "/api/delete/project";
    final String list = "/api/list/project";
    final String reassign = "/api/reassign/project";
    final String getClient = "/api/getclient/project";
    final String getContainers = "/api/containers/project";
    final String auth = "Authorization";
    final String oid = "oid";

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
    AdminJpaService adminJpaService;
    @Autowired
    BasicUserJpaService basicUserJpaService;
    @Autowired
    ProjectHolderJpaService projectHolderJpaService;
    @Autowired
    ProjectJpaService projectJpaService;
    @Autowired
    ContainerJpaService containerJpaService;
    @Autowired
    AuthJpaService authJpaService;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    JwtTokenJpaService jwtTokenJpaService;


    Container container;
    Container container1;
    Project project;
    ProjectDto projectDto;
    Project project1;
    ProjectDto projectDto1;
    ProjectHolder projectHolder;
    ProjectHolderDto projectHolderDto;
    ProjectHolder projectHolder1;
    ProjectHolderDto projectHolderDto1;
    User user;
    BasicUser basicUser;
    UserDto basicUserDto;
    BasicUser basicUser1;
    UserDto basicUserDto1;
    Admin admin;
    UserDto adminDto;
    BasicUser newUser;

    String userToken;
    String userToken1;
    String adminToken;
    String projectJson;

    String expected;
    ProjectDto expectedDto;


    @BeforeEach
    void setUp()
            throws JsonProcessingException, FileNotFoundException, ExistsException, DateException,
            NotFoundException, AuthorityException {

        // arrange
        File file = new File("src/test/resources/Tokens.txt");
        FileReader fr = new FileReader(file);
        Scanner sc = new Scanner(fr);
        adminToken = sc.nextLine();
        userToken = sc.nextLine();
        userToken1 = sc.nextLine();
        sc.close();

        user = jwtTokenJpaService.parseToken(adminToken);
        admin = adminJpaService.findById(user.getId());
        adminDto = new UserDto(admin);

        user = jwtTokenJpaService.parseToken(userToken);
        basicUser = basicUserJpaService.findById(user.getId());
        basicUserDto = new UserDto(basicUser);

        user = jwtTokenJpaService.parseToken(userToken1);
        basicUser1 = basicUserJpaService.findById(user.getId());
        basicUserDto1 = new UserDto(basicUser1);

        projectHolder = basicUser.getProjectHolder();
        projectHolderDto = new ProjectHolderDto(projectHolder);
        projectHolder1 = basicUser1.getProjectHolder();
        projectHolderDto1 = new ProjectHolderDto(projectHolder1);

        project = projectJpaService
                .register(new Project("X", "project 1"),
                        projectHolderDto,
                        adminDto);
        projectDto = new ProjectDto(project.getId());
        projectJson = objectWriter.writeValueAsString(projectDto);

        container = containerJpaService
                .register(new Container(23.0, "desc", "name"),
                        projectDto);
        container1 = containerJpaService
                .register(new Container(24.0, "desc1", "name1"),
                        projectDto);

    }

    /**
     * create project.
     *
     * @throws Exception Exception
     */
    @WithMockUser(authorities = authority)
    @Test
    void createProject() throws Exception {

        // arrange
        getAuthentication(true);
        projectDto1 =
                new ProjectDto("Y", "project1",
                        projectHolder.getId(), admin.getId());
        String projectJson1 = objectWriter.writeValueAsString(projectDto1);

        // act and assert
        mvc.perform(post(create)
                .contentType(MediaType.APPLICATION_JSON)
                .content(projectJson1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(projectDto1.getTitle()))
                .andExpect(jsonPath("$.description").value(projectDto1.getDescription()))
                .andExpect(jsonPath("$.projectHolderId").value(projectDto1.getProjectHolderId()))
                .andExpect(jsonPath("$.adminId").value(projectDto1.getAdminId()));
    }

    /**
     * create project without authorization.
     *
     * @throws Exception Exception
     */
    @Test
    void createWithoutAuthorization() throws Exception {

        // arrange
        getAuthentication(true);
        projectDto1 =
                new ProjectDto("A", "unauthorized",
                        projectHolder.getId(), admin.getId());
        String projectJson1 = objectWriter.writeValueAsString(projectDto1);

        // act and assert
        mvc.perform(post(create)
                .contentType(MediaType.APPLICATION_JSON)
                .content(projectJson1))
                .andExpect(status().isUnauthorized());
    }

    /**
     * get project.
     *
     * @throws Exception Exception
     */
    @Test
    void getProject() throws Exception {

        // arrange
        getAuthentication(false);
        expectedDto = new ProjectDto(project);
        expected = objectWriter.writeValueAsString(expectedDto);

        // act and assert
        mvc.perform(post(get)
                .header(oid, basicUser.getId())
                .header(auth, userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(projectJson))
                .andExpect(status().isOk()).andExpect(content().json(expected));
    }

    /**
     * get project without authorization.
     *
     * @throws Exception Exception
     */
    @Test
    void getWithoutAuthorization() throws Exception {

        // arrange
        getAuthentication(true);

        // act and assert
        mvc.perform(post(get)
                .header(oid, basicUser.getId())
                .header(auth, userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(projectJson))
                .andExpect(status().isUnauthorized());
    }

    /**
     * update project.
     *
     * @throws Exception Exception
     */
    @WithMockUser(authorities = authority)
    @Test
    void updateProject() throws Exception {

        // act and assert
        getAuthentication(true);
        projectDto.setDescription("new desc");
        projectDto.setTitle("new title");
        expectedDto = new ProjectDto(project);
        expected = objectWriter.writeValueAsString(expectedDto);

        // act and assert
        mvc.perform(post(update)
                .contentType(MediaType.APPLICATION_JSON)
                .content(expected)).andExpect(status().isOk()).andExpect(content().json(expected));
    }

    /**
     * update project without authorization.
     *
     * @throws Exception Exception
     */
    @Test
    void updateWithoutAuthorization() throws Exception {

        // arrange
        getAuthentication(true);
        projectDto.setDescription("mal desc");
        projectDto.setTitle("mal title");
        expectedDto = new ProjectDto(project);
        expected = objectWriter.writeValueAsString(expectedDto);

        // act and assert
        mvc.perform(post(update)
                .contentType(MediaType.APPLICATION_JSON)
                .content(expected)).andExpect(status().isUnauthorized());
    }

    /**
     * delete project.
     *
     * @throws Exception Exception
     */
    @WithMockUser(authorities = authority)
    @Test
    void deleteProject() throws Exception {

        // arrange
        getAuthentication(true);

        // act and assert
        mvc.perform(post(delete)
                .contentType(MediaType.APPLICATION_JSON)
                .content(projectJson))
                .andExpect(status().isOk())
                .andExpect(content().json(projectJson));
        assertThat(projectJpaService.count()).isEqualTo(0);
        assertThat(containerJpaService.count()).isEqualTo(0);
        assertThat(projectHolderJpaService.count()).isEqualTo(2);
        assertThat(projectHolderJpaService
                .findById(projectHolder.getId()).getProjects().size())
                .isEqualTo(0);
    }

    /**
     * delete project without authorization.
     *
     * @throws Exception Exception
     */
    @Test
    void deleteWithoutAuthorization() throws Exception {

        // arrange
        getAuthentication(true);

        // act and assert
        mvc.perform(post(delete)
                .contentType(MediaType.APPLICATION_JSON)
                .content(projectJson)).andExpect(status().isUnauthorized());
    }

    /**
     * reassign project to different client.
     *
     * @throws Exception Exception
     */
    @WithMockUser(authorities = authority)
    @Test
    void changeProject() throws Exception {

        // arrange
        getAuthentication(true);

        projectDto
                .setProjectHolderId(projectHolder1.getId());
        projectJson = objectWriter.writeValueAsString(projectDto);

        expectedDto = new ProjectDto(project);
        expectedDto.setProjectHolderId(projectHolder1.getId());
        expected = objectWriter.writeValueAsString(expectedDto);

        // act and assert
        mvc.perform(post(reassign)
                .contentType(MediaType.APPLICATION_JSON)
                .content(projectJson))
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    /**
     * reassign project to different client without authorization.
     *
     * @throws Exception Exception
     */
    @Test
    void changeProjectWithoutAuthorization() throws Exception {

        // arrange
        getAuthentication(true);
        projectDto
                .setProjectHolderId(projectHolder1.getId());
        projectJson = objectWriter.writeValueAsString(projectDto);

        // act and assert
        mvc.perform(post(reassign)
                .contentType(MediaType.APPLICATION_JSON)
                .content(projectJson)).andExpect(status().isUnauthorized());
    }

    /**
     * list projects.
     *
     * @throws Exception Exception
     */
    @WithMockUser(authorities = authority)
    @Test
    void getProjects() throws Exception {

        // arrange
        getAuthentication(true);
        project1 = projectJpaService
                .register(new Project("new title", "new project"),
                        projectHolderDto1,
                        adminDto);
        List<ProjectDto> projectDtos = projectJpaService.findAllDtos(ProjectDto.class);
        expected = objectWriter.writeValueAsString(projectDtos);

        // act and assert
        mvc.perform(post(list))
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    /**
     * list projects without authorization.
     *
     * @throws Exception Exception
     */
    @Test
    void getProjectsWithoutAuthorization() throws Exception {

        // arrange
        getAuthentication(true);

        // act and assert
        mvc.perform(post(list)).andExpect(status().isUnauthorized());
    }


    /**
     * get Client.
     *
     * @throws Exception Exception
     */
    @WithMockUser(authorities = authority)
    @Test
    void getClientTest() throws Exception {

        // arrange
        getAuthentication(true);
        expected = objectWriter.writeValueAsString(basicUserDto);

        // act and assert
        mvc.perform(post(getClient)
                .contentType(MediaType.APPLICATION_JSON)
                .content(projectJson))
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    /**
     * get Client without authorization.
     *
     * @throws Exception Exception
     */
    @Test
    void getClientWithoutAuthorization() throws Exception {

        // arrange
        getAuthentication(true);

        // act and assert
        mvc.perform(post(getClient)
                .contentType(MediaType.APPLICATION_JSON)
                .content(projectJson)).andExpect(status().isUnauthorized());
    }

    /**
     * get containers of project.
     *
     * @throws Exception Exception
     */
    @Test
    void getContainersInProject() throws Exception {

        // arrange
        getAuthentication(false);

        List<ContainerDto> containerDtos =
                projectJpaService.getContainerDtosInProject(projectDto);
        expected = objectWriter.writeValueAsString(containerDtos);

        // act and assert
        mvc.perform(post(getContainers)
                .header(oid, basicUser.getId())
                .header(auth, userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(projectJson))
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    /**
     * get containers of project without authorization.
     *
     * @throws Exception Exception
     */
    @Test
    void getContainersInProjectWithoutAuthorization() throws Exception {

        // arrange
        getAuthentication(true);

        // act and assert
        mvc.perform(post(getContainers)
                .header(oid, basicUser.getId())
                .header(auth, userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(projectJson)).andExpect(status().isUnauthorized());
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