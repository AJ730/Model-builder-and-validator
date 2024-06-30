package nl.tudelft.sp.modelchecker.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
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
class ProjectHolderControllerTest {

    final String authority = "APPROLE_ADMIN";
    final String get = "/api/get/projectholder";
    final String userProjectholder = "/api/user/projectholder";
    final String projectsProjectholder = "/api/projects/projectholder";
    final String delete = "/api/delete/projectholder";
    final String list = "/api/list/projectholder";
    final String projectsUserProjectholder = "/api/projects/user/projectholder";
    final String oid = "oid";
    final String auth = "Authorization";

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
    BasicUserJpaService basicUserJpaService;
    @Autowired
    AdminJpaService adminJpaService;
    @Autowired
    AuthJpaService authJpaService;
    @Autowired
    ProjectHolderJpaService projectHolderJpaService;
    @Autowired
    ProjectJpaService projectJpaService;
    @Autowired
    JwtTokenJpaService jwtTokenJpaService;

    ProjectHolder projectHolder;
    ProjectHolderDto projectHolderDto;
    ProjectHolderDto expectedProjectHolderDto;
    Project project;
    Project project1;
    User user;
    User user1;
    BasicUser basicUser;
    UserDto basicUserDto;
    Admin admin;
    UserDto adminDto;


    String adminToken;
    String userToken;
    String userToken1;
    String projectHolderJson;
    String basicUserJson;
    String expected;

    @BeforeEach
    void setUp()
            throws JsonProcessingException, ExistsException, DateException, FileNotFoundException,
            NotFoundException, AuthorityException {

        // arrange
        File file = new File("src/test/resources/Tokens.txt");
        FileReader fr = new FileReader(file);
        Scanner sc = new Scanner(fr);
        adminToken = sc.nextLine();
        userToken = sc.nextLine();
        userToken1 = sc.nextLine();
        sc.close();

        user = jwtTokenJpaService.parseToken(userToken);
        basicUser = basicUserJpaService.findById(user.getId());
        basicUserDto = new UserDto(basicUser);

        user = jwtTokenJpaService.parseToken(adminToken);
        admin = adminJpaService.findById(user.getId());
        adminDto = new UserDto(admin);

        projectHolder = basicUser.getProjectHolder();
        projectHolderDto = new ProjectHolderDto(projectHolder.getId());

        project = projectJpaService
                .register(new Project("X", "project 1"),
                        projectHolderDto,
                        adminDto);
        project1 = projectJpaService
                .register(new Project("Y", "project 2"),
                        projectHolderDto,
                        adminDto);

        projectHolderJson = objectWriter.writeValueAsString(projectHolderDto);
        basicUserJson = objectWriter.writeValueAsString(basicUserDto);

    }

    /**
     * delete project holder.
     *
     * @throws Exception Exception
     */
    @WithMockUser(authorities = authority)
    @Test
    void deleteTest() throws Exception {

        // arrange
        getAuthentication(true);
        assertThat(projectJpaService.count()).isEqualTo(2);

        // act and assert
        mvc.perform(post(delete)
                .contentType(MediaType.APPLICATION_JSON)
                .content(projectHolderJson))
                .andExpect(status().isOk())
                .andExpect(content().json(projectHolderJson));
        assertThat(projectHolderJpaService.count()).isEqualTo(0);
        assertThat(projectJpaService.count()).isEqualTo(0);
    }

    /**
     * delete project holder without authorization.
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
                .content(projectHolderJson))
                .andExpect(status().isUnauthorized());
    }

    /**
     * list project holders.
     *
     * @throws Exception Exception
     */
    @WithMockUser(authorities = authority)
    @Test
    void getProjectHolders() throws Exception {
        getAuthentication(true);

        // arrange
        user1 = jwtTokenJpaService.parseToken(userToken1);
        List<ProjectHolderDto> dtos =
                projectHolderJpaService.findAllDtos(ProjectHolderDto.class);
        String expected = objectWriter.writeValueAsString(dtos);

        // act and assert
        mvc.perform(post(list))
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    /**
     * list project holders without authorization.
     *
     * @throws Exception Exception
     */
    @Test
    void getProjectHoldersWithoutAuthorization() throws Exception {

        // arrange
        getAuthentication(true);

        // act and assert
        mvc.perform(post(list))
                .andExpect(status().isUnauthorized());
    }

    /**
     * get project holder.
     *
     * @throws Exception Exception
     */
    @WithMockUser(authorities = authority)
    @Test
    void getProjectHolder() throws Exception {
        getAuthentication(true);

        // arrange
        expectedProjectHolderDto = new ProjectHolderDto(projectHolder);
        expected = objectWriter.writeValueAsString(expectedProjectHolderDto);

        // act and assert
        mvc.perform(post(get)
                .contentType(MediaType.APPLICATION_JSON)
                .content(projectHolderJson))
                .andExpect(status().isOk())
                .andExpect(content().json(expected));

    }

    /**
     * get project holder without authorization.
     *
     * @throws Exception Exception
     */
    @Test
    void getProjectHolderWithoutAuthorization() throws Exception {

        // arrange
        getAuthentication(true);

        // act and assert
        mvc.perform(post(get)
                .contentType(MediaType.APPLICATION_JSON)
                .content(projectHolderJson))
                .andExpect(status().isUnauthorized());
    }

    /**
     * get project holder of user.
     *
     * @throws Exception Exception.
     */
    @WithMockUser(authorities = authority)
    @Test
    void testGetProjectHolderOfUser() throws Exception {

        // arrange
        getAuthentication(true);

        // act and assert
        mvc.perform(post(userProjectholder)
                .contentType(MediaType.APPLICATION_JSON)
                .content(basicUserJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(projectHolder.getId()))
                .andExpect(jsonPath("$.userId").value(basicUser.getId()));
    }

    /**
     * get project holder of user without authorization.
     *
     * @throws Exception Exception.
     */
    @Test
    void testGetProjectHolderOfUserWithoutAuthorization() throws Exception {

        // arrange
        getAuthentication(true);

        // act and assert
        mvc.perform(post(userProjectholder)
                .contentType(MediaType.APPLICATION_JSON)
                .content(basicUserJson))
                .andExpect(status().isUnauthorized());
    }

    /**
     * get projects of project holder.
     *
     * @throws Exception Exception
     */
    @Test
    void getProjectsInProjectHolder() throws Exception {

        // arrange
        getAuthentication(false);

        List<ProjectDto> dtos = projectHolderJpaService
                .getProjectDtosInProjectHolder(projectHolderDto);
        String expected = objectWriter.writeValueAsString(dtos);

        // act and assert
        mvc.perform(post(projectsProjectholder)
                .header(oid, basicUser.getId())
                .header(auth, userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(projectHolderJson))
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    /**
     * get projects of project holder without authorization.
     *
     * @throws Exception Exception
     */
    @Test
    void getProjectsInProjectHolderWithoutAuthorization() throws Exception {

        // arrange
        getAuthentication(true);

        // act and assert
        mvc.perform(post(projectsProjectholder)
                .header(oid, basicUser.getId())
                .header(auth, userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(projectHolderJson))
                .andExpect(status().isUnauthorized());
    }

    /**
     * get projects of client.
     *
     * @throws Exception Exception
     */
    @Test
    void getProjectsClientInProjectHolder() throws Exception {

        // arrange
        getAuthentication(false);
        List<ProjectDto> projectDtos = projectHolderJpaService
                .getProjectDtosInProjectHolder(projectHolderDto);
        String expected = objectWriter.writeValueAsString(projectDtos);

        // act and assert
        mvc.perform(post(projectsUserProjectholder)
                .header(oid, basicUser.getId())
                .header(auth, userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(basicUserJson))
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    /**
     * get projects of client without authorization.
     *
     * @throws Exception Exception
     */
    @Test
    void getProjectsClientInProjectHolderWithoutAuthorization() throws Exception {

        // arrange
        getAuthentication(true);

        // act and assert
        mvc.perform(post(projectsUserProjectholder)
                .header(oid, basicUser.getId())
                .header(auth, userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(basicUserJson))
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
}