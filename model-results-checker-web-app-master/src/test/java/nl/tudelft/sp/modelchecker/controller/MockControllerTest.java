package nl.tudelft.sp.modelchecker.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.IOException;
import java.util.ArrayList;
import nl.tudelft.sp.modelchecker.Application;
import nl.tudelft.sp.modelchecker.SecurityFilter.MockSpringSecurityFilter;
import nl.tudelft.sp.modelchecker.dto.RecordListDto;
import nl.tudelft.sp.modelchecker.exceptions.DateException;
import nl.tudelft.sp.modelchecker.exceptions.ExistsException;
import nl.tudelft.sp.modelchecker.services.servicebeans.ContainerJpaService;
import nl.tudelft.sp.modelchecker.services.servicebeans.JwtTokenJpaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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
public class MockControllerTest {

    private final String getUserInfo = "/api/get/UserInfo";
    private final String saveCsv = "/api/save/csv";

    private final String authorization = "Authorization";
    private final String token = "token";


    @Autowired
    WebApplicationContext context;
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ObjectWriter objectWriter;

    @MockBean
    JwtTokenJpaService jwtTokenJpaService;

    @MockBean
    ContainerJpaService containerJpaService;

    @InjectMocks
    AuthController authController;

    /**
     * test create new user in the past.
     *
     * @throws Exception Exception
     */
    @Test
    void registerNewUserInThePast()
            throws Exception {

        // arrange
        getAuthentication(false);
        when(jwtTokenJpaService.parseToken(token)).thenThrow(DateException.class);

        // act and assert
        mvc.perform(get(getUserInfo)
                .header(authorization, token))
                .andExpect(status().isInternalServerError())
                .andExpect(result ->
                        assertThat(result.getResolvedException())
                                .isInstanceOf(DateException.class));
    }

    /**
     * register existing user test.
     *
     * @throws Exception Exception
     */
    @Test
    void registerExistingUserTest()
            throws Exception {

        // arrange
        getAuthentication(false);
        when(jwtTokenJpaService.parseToken(token)).thenThrow(ExistsException.class);

        // act and assert
        mvc.perform(get(getUserInfo)
                .header(authorization, token))
                .andExpect(status().isInternalServerError())
                .andExpect(result ->
                        assertThat(result.getResolvedException())
                                .isInstanceOf(ExistsException.class));
    }

    /**
     * Test IOException handler.
     *
     * @throws Exception Exception
     */
    @Test
    void badWeatherSaveCsvTest()
            throws Exception {
        getAuthentication(false);
        // arrange
        RecordListDto recordListDto = new RecordListDto(new ArrayList<>(), 1L);
        String recordListDtoJson = objectWriter.writeValueAsString(recordListDto);
        when(containerJpaService.overideContainer(recordListDto)).thenThrow(IOException.class);

        // act and assert
        mvc.perform(post(saveCsv)
                .contentType(MediaType.APPLICATION_JSON)
                .content(recordListDtoJson))
                .andExpect(status().isInternalServerError())
                .andExpect(result ->
                        assertThat(result.getResolvedException())
                                .isInstanceOf(IOException.class));
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
