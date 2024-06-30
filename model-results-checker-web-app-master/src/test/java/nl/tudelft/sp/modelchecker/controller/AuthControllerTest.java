package nl.tudelft.sp.modelchecker.controller;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import nl.tudelft.sp.modelchecker.Application;
import nl.tudelft.sp.modelchecker.SecurityFilter.MockSpringSecurityFilter;
import nl.tudelft.sp.modelchecker.dto.UserDto;
import nl.tudelft.sp.modelchecker.entities.User;
import nl.tudelft.sp.modelchecker.services.servicebeans.AdminJpaService;
import nl.tudelft.sp.modelchecker.services.servicebeans.AuthJpaService;
import nl.tudelft.sp.modelchecker.services.servicebeans.JwtTokenJpaService;
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
class AuthControllerTest {

    final String register = "/api/get/UserInfo";
    final String getOid = "/api/get/oid";

    @Autowired
    WebApplicationContext context;
    MockMvc mvc;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    AdminJpaService adminJpaService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    ObjectWriter objectWriter;
    @Autowired
    AuthJpaService authJpaService;
    @Autowired
    JwtTokenJpaService jwtTokenJpaService;

    String auth = "Authorization";
    String mockToken;
    User tan;
    UserDto userDto;
    String expected;
    String emailHeader = "email";
    String email = "tandao311@gmail.com";
    String username = "Tan dao";

    @BeforeEach
    void setUp() throws FileNotFoundException {

        // arrange
        File file = new File("src/test/resources/Tokens.txt");
        FileReader fr = new FileReader(file);
        Scanner sc = new Scanner(fr);
        mockToken = sc.nextLine();
        sc.close();
    }

    /**
     * register new user.
     *
     * @throws Exception Exception
     */
    @Test
    void registerNewUserTest()
            throws Exception {

        // arrange
        getAuthentication(false);

        // act and assert
        mvc.perform(get(register)
                .header(auth, mockToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.username").value(username));
    }

    /**
     * register new without authorization.
     *
     * @throws Exception Exception
     */
    @Test
    void unauthorizedRegisterNewUser() throws Exception {

        // arrange
        getAuthentication(true);

        // act and assert
        mvc.perform(get(register)
                .header(auth, mockToken))
                .andExpect(status().isUnauthorized());
    }

    /**
     * get user info.
     *
     * @throws Exception Exception
     */
    @Test
    void getUserInfo() throws Exception {

        // arrange
        getAuthentication(false);
        tan = jwtTokenJpaService.parseToken(mockToken);
        userDto = new UserDto(tan);
        expected = objectWriter.writeValueAsString(userDto);

        // act and assert
        mvc.perform(get(getOid)
                .param(emailHeader, email))
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    /**
     * get user without authorization.
     *
     * @throws Exception Exception
     */
    @Test
    void getUserWithoutAuthorization() throws Exception {

        // arrange
        getAuthentication(true);
        tan = jwtTokenJpaService.parseToken(mockToken);
        userDto = new UserDto(tan);
        expected = objectWriter.writeValueAsString(userDto);

        // act and assert
        mvc.perform(get(getOid)
                .param(emailHeader, email))
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