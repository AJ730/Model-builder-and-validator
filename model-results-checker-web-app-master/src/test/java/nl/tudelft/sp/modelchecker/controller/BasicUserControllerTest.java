package nl.tudelft.sp.modelchecker.controller;

import static org.assertj.core.api.Assertions.assertThat;
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
import nl.tudelft.sp.modelchecker.entities.BasicUser;
import nl.tudelft.sp.modelchecker.exceptions.DateException;
import nl.tudelft.sp.modelchecker.exceptions.ExistsException;
import nl.tudelft.sp.modelchecker.services.servicebeans.BasicUserJpaService;
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
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
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
class BasicUserControllerTest {

    final String authority = "APPROLE_ADMIN";
    final String get = "/api/get/user";
    final String update = "/api/update/user";
    final String delete = "/api/delete/user";
    final String list = "/api/list/user";

    @Autowired
    WebApplicationContext context;
    MockMvc mvc;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    BasicUserJpaService basicUserJpaService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    ObjectWriter objectWriter;

    String rqJson;
    String id = "id";
    String email = "basic@gmail.com";
    String username = "basic";
    Date date;
    BasicUser user;
    UserDto userDto;
    String basicUserJson;
    String expected;

    @BeforeEach
    void setUp() throws JsonProcessingException, ExistsException, DateException {

        // arrange
        date = Date.valueOf(LocalDate.now());
        user = basicUserJpaService.registerUser(new BasicUser(id, username, email, date));
        userDto = new UserDto(user);
        rqJson = objectWriter.writeValueAsString(userDto);
        basicUserJson = objectWriter.writeValueAsString(userDto);
    }

    /**
     * get basic user.
     *
     * @throws Exception Exception
     */
    @Test
    void getUser() throws Exception {

        // arrange
        getAuthentication(false);
        expected = objectWriter.writeValueAsString(userDto);

        // act and assert
        mvc.perform(post(get)
                .contentType(MediaType.APPLICATION_JSON)
                .content(basicUserJson))
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    /**
     * get basic user without authorization.
     *
     * @throws Exception Exception
     */
    @Test
    void getNonExsistentUser() throws Exception {

        // arrange
        getAuthentication(false);

        BasicUser basicUser = new BasicUser("20L", "test", "test@gmail.com",
                Date.valueOf(LocalDate.now()));

        basicUserJson = objectWriter.writeValueAsString(new UserDto(basicUser));

        // act and assert
        mvc.perform(post(get)
                .contentType(MediaType.APPLICATION_JSON)
                .content(basicUserJson))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(AuthenticationCredentialsNotFoundException.class));
    }


    /**
     * update user.
     *
     * @throws Exception Exception
     */
    @WithMockUser(authorities = authority)
    @Test
    void updateUserTest() throws Exception {

        // arrange
        getAuthentication(true);
        String updatedUserJson = updateUser(user, "new");

        // act and assert
        mvc.perform(post(update)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedUserJson))
                .andExpect(status().isOk())
                .andExpect(content().json(updatedUserJson));
    }

    /**
     * update user without authorization.
     *
     * @throws Exception Exception
     */
    @Test
    void updateUserWithoutAuthorization() throws Exception {

        // arrange
        getAuthentication(true);
        String updatedUserJson = updateUser(user, "new");

        // act and assert
        mvc.perform(post(update)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedUserJson))
                .andExpect(status().isUnauthorized());
    }

    /**
     * delete user.
     *
     * @throws Exception Exception
     */
    @WithMockUser(authorities = authority)
    @Test
    void deleteUser() throws Exception {

        // arrange
        getAuthentication(true);

        // act and assert
        mvc.perform(post(delete)
                .contentType(MediaType.APPLICATION_JSON)
                .content(basicUserJson))
                .andExpect(status().isOk())
                .andExpect(content().json(basicUserJson));
        assertThat(basicUserJpaService.count()).isEqualTo(0);
        assertThat(basicUserJpaService.findById(user.getId())).isNull();
    }

    /**
     * delete user without authorization.
     *
     * @throws Exception Exception
     */
    @Test
    void deleteUserWithoutAuthorization() throws Exception {

        // arrange
        getAuthentication(true);

        // act and assert
        mvc.perform(post(delete)
                .contentType(MediaType.APPLICATION_JSON)
                .content(basicUserJson))
                .andExpect(status().isUnauthorized());
    }


    /**
     * list users.
     *
     * @throws Exception Exception
     */
    @WithMockUser(authorities = authority)
    @Test
    void listUser() throws Exception {

        // arrange
        getAuthentication(true);
        basicUserJpaService
                .registerUser(new BasicUser("id1", "user1", "us1@gmail.com", date));
        List<UserDto> userDtos = basicUserJpaService.convertToUserDtos();
        expected = objectWriter.writeValueAsString(userDtos);

        // act and assert
        mvc.perform(post(list))
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    /**
     * list users without authorization.
     *
     * @throws Exception Exception
     */
    @Test
    void listUserWithoutAuthorization() throws Exception {

        // arrange
        getAuthentication(true);
        List<UserDto> userDtos = basicUserJpaService.convertToUserDtos();
        expected = objectWriter.writeValueAsString(userDtos);

        // act and assert
        mvc.perform(post(list))
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
     * update user.
     *
     * @param user            user
     * @param updatedUsername updatedUsername
     * @return updated userDto json
     * @throws JsonProcessingException JsonProcessingException
     */
    private String updateUser(BasicUser user, String updatedUsername)
            throws JsonProcessingException {
        user.setUsername(updatedUsername);
        UserDto updatedUserDto = new UserDto(user);
        return objectWriter.writeValueAsString(updatedUserDto);
    }

}