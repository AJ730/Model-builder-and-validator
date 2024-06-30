package nl.tudelft.sp.modelchecker.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import nl.tudelft.sp.modelchecker.Application;
import nl.tudelft.sp.modelchecker.SecurityFilter.MockSpringSecurityFilter;
import nl.tudelft.sp.modelchecker.dto.UserDto;
import nl.tudelft.sp.modelchecker.entities.Admin;
import nl.tudelft.sp.modelchecker.exceptions.DateException;
import nl.tudelft.sp.modelchecker.exceptions.ExistsException;
import nl.tudelft.sp.modelchecker.services.servicebeans.AdminJpaService;
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
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Transactional
class AdminControllerTest {

    final String authority = "APPROLE_ADMIN";
    final String get = "/api/get/admin";
    final String update = "/api/update/admin";
    final String delete = "/api/delete/admin";
    final String list = "/api/list/admin";

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
    AdminJpaService adminService;

    String id = "id";
    String email = "ad@gmail.com";
    String username = "ad";
    Date date;
    String updatedUserDtoJson;
    Admin user;
    UserDto userDto;
    String userJson;
    String expectedJson;

    @BeforeEach
    void setUp() throws JsonProcessingException, ExistsException, DateException {

        // arrange
        date = Date.valueOf(LocalDate.now());
        user = adminService.registerUser(new Admin(id, username, email, date));
        userDto = new UserDto(user);
        userJson = objectWriter.writeValueAsString(userDto);
        getAuthentication(true);
    }

    /**
     * Test get Admin.
     *
     * @throws Exception Exception
     */
    @WithMockUser(authorities = authority)
    @Test
    void getAdmin() throws Exception {

        // arrange
        expectedJson = objectWriter.writeValueAsString(userDto);

        // act and assert
        mvc.perform(post(get)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    /**
     * Test get nonexistent admin.
     *
     * @throws Exception Exception
     */
    @WithMockUser(authorities = authority)
    @Test
    void getNonExistentAdmin() throws Exception {

        // arrange
        Admin unregistered =
                new Admin("id1",
                        "username",
                        "email@gmail.com",
                        Date.valueOf(LocalDate.now()));
        UserDto unregisteredDto = modelMapper.map(unregistered, UserDto.class);
        userJson = objectWriter.writeValueAsString(unregisteredDto);

        // act and assert
        mvc.perform(post(get)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(AuthenticationCredentialsNotFoundException.class));
    }

    /**
     * update admin.
     *
     * @throws Exception Exception
     */
    @WithMockUser(authorities = authority)
    @Test
    void updateAdminTest() throws Exception {

        // arrange
        updatedUserDtoJson =
                updateAdmin(user, "updatedUsername",
                        "updated@gmail.com");

        // act and assert
        mvc.perform(post(update)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedUserDtoJson))
                .andExpect(status().isOk())
                .andExpect(content().json(updatedUserDtoJson));
    }

    /**
     * unauthorized operation: update admin.
     *
     * @throws Exception Exception
     */
    @Test
    void updateAdminWithoutAuthorization() throws Exception {

        // arrange
        updatedUserDtoJson =
                updateAdmin(user, "abc",
                        "abc@gmail.com");

        // act and assert
        mvc.perform(post(update)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedUserDtoJson))
                .andExpect(status().isUnauthorized());
    }

    /**
     * delete admin.
     *
     * @throws Exception Exception
     */
    @WithMockUser(authorities = authority)
    @Test
    void deleteAdmin() throws Exception {

        // arrange
        String expected = objectWriter.writeValueAsString(userDto);

        // act and assert
        mvc.perform(post(delete)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
        assertThat(adminService.findById(user.getId())).isNull();
        assertThat(adminService.count()).isEqualTo(0);
    }

    /**
     * unauthorized operation: delete admin.
     *
     * @throws Exception Exception
     */
    @Test
    void deleteAdminWithoutAuthorization() throws Exception {

        // act and assert
        mvc.perform(post(delete)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isUnauthorized());
        assertThat(adminService.findById(user.getId())).isNotNull();
        assertThat(adminService.count()).isEqualTo(1);
    }

    /**
     * list admins.
     *
     * @throws Exception Exception
     */
    @WithMockUser(authorities = authority)
    @Test
    void listAdmin() throws Exception {

        // arrange
        Admin admin1 =
                adminService.registerUser(new Admin("id1",
                        "ad1", "ad1@gmail.com", date));
        List<UserDto> userDtos = adminService.convertToUserDtos();
        String expected = objectWriter.writeValueAsString(userDtos);

        // act and assert
        mvc.perform(post(list))
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
        assertThat(adminService.count()).isEqualTo(2);
    }

    /**
     * list without authorization.
     *
     * @throws Exception Exception
     */
    @Test
    void listAdminWithoutAuthorization() throws Exception {

        // act and assert
        mvc.perform(post(list))
                .andExpect(status().isUnauthorized());
    }

    /**
     * get admin without authorization.
     *
     * @throws Exception Exception
     */
    @Test
    void getAdminWithoutAuthorization() throws Exception {

        // act and assert
        mvc.perform(get(get)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
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
     * update admin.
     *
     * @param admin       admin
     * @param newUsername newUsername
     * @param newEmail    newEmail
     * @return updated userDto json
     * @throws JsonProcessingException JsonProcessingException
     */
    private String updateAdmin(Admin admin, String newUsername, String newEmail)
            throws JsonProcessingException {
        admin.setEmail(newEmail);
        admin.setUsername(newUsername);
        UserDto updated = new UserDto(admin);
        return objectWriter.writeValueAsString(updated);
    }
}