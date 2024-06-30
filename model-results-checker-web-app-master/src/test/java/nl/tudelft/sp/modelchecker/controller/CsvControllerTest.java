package nl.tudelft.sp.modelchecker.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import javassist.NotFoundException;
import nl.tudelft.sp.modelchecker.Application;
import nl.tudelft.sp.modelchecker.SecurityFilter.MockSpringSecurityFilter;
import nl.tudelft.sp.modelchecker.dto.*;
import nl.tudelft.sp.modelchecker.entities.Container;
import nl.tudelft.sp.modelchecker.entities.Csv;
import nl.tudelft.sp.modelchecker.entities.Record;
import nl.tudelft.sp.modelchecker.exceptions.ExistsException;
import nl.tudelft.sp.modelchecker.services.servicebeans.ContainerJpaService;
import nl.tudelft.sp.modelchecker.services.servicebeans.CsvJpaService;
import nl.tudelft.sp.modelchecker.services.servicebeans.RecordJpaService;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Transactional
class CsvControllerTest {

    final String authority = "APPROLE_ADMIN";
    private final String get = "/api/get/csv";
    private final String records = "/api/records/csv";
    private final String delete = "/api/delete/csv";
    private final String list = "/api/list/csv";
    private final String save = "/api/save/csv";
    private final String deleteRecords = "/api/delete/records";
    @Autowired
    WebApplicationContext context;
    MockMvc mvc;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    CsvJpaService csvJpaService;
    @Autowired
    RecordJpaService recordJpaService;
    @Autowired
    ContainerJpaService containerJpaService;
    @Autowired
    ObjectWriter objectWriter;
    @Autowired
    ObjectMapper objectMapper;
    private Container container;
    private ContainerDto containerDto;
    private String containerJson;

    private Csv csv;
    private CsvDto csvDto;
    private String csvJson;

    private MultipartFile classes;

    private Record record;
    private Record record1;


    /**
     * Set up an environment.
     *
     * @throws ExistsException ExistsException
     */
    @BeforeEach
    void setUp() throws ExistsException, IOException, NotFoundException,
            SQLIntegrityConstraintViolationException {

        // arrange
        File textFile = new File("src/test/resources/classes.txt");
        FileInputStream input3 = new FileInputStream(textFile);

        classes = new MockMultipartFile(
                "classes.txt", "classes.txt",
                "text/plain", input3.readAllBytes()
        );

        input3.close();

        container = containerJpaService.register(new Container(23.0,
                "tesc", "desc"));
        containerDto = new ContainerDto(container);
        containerJpaService.registerClasses(classes, containerDto);

        container = containerJpaService.findById(container.getId());
        containerDto = new ContainerDto(container);

        containerJson = objectWriter.writeValueAsString(containerDto);

        csv = csvJpaService.register(new Csv(), containerDto);
        csvDto = new CsvDto(csv);
        csvJson = objectWriter.writeValueAsString(csvDto);

        record = Record.builder().frameNum(2)
                .objectId(1).trackerL(3).label("testLabel1")
                .trackerH(3).trackerW(5).trackerT(3).build();

        record1 = Record.builder().frameNum(2)
                .objectId(4).trackerL(6).label("testLabel2")
                .trackerH(3).trackerW(7).trackerT(3).build();

        record = recordJpaService.register(record, csvDto);
        record1 = recordJpaService.register(record1, csvDto);

    }

    /**
     * get Csv.
     *
     * @throws Exception Exception
     */
    @Test
    void getCsv() throws Exception {

        // arrange
        getAuthentication(false);

        String expectedStr = objectWriter.writeValueAsString(csvDto);

        // act and assert
        mvc.perform(post(get)
                .contentType(MediaType.APPLICATION_JSON)
                .content(csvJson))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedStr));
    }

    /**
     * get Csv without authorization.
     *
     * @throws Exception Exception
     */
    @Test
    void getWithoutAuthorization() throws Exception {

        // arrange
        getAuthentication(true);

        // act and assert
        mvc.perform(post(get)
                .contentType(MediaType.APPLICATION_JSON)
                .content(csvJson))
                .andExpect(status().isUnauthorized());
    }

    /**
     * delete Csv.
     *
     * @throws Exception Exception
     */
    @WithMockUser(authorities = authority)
    @Test
    void deleteCsv() throws Exception {

        // arrange
        getAuthentication(true);

        // act and assert
        mvc.perform(post(delete)
                .contentType(MediaType.APPLICATION_JSON)
                .content(csvJson))
                .andExpect(status().isOk())
                .andExpect(content().json(csvJson));

        assertThat(csvJpaService.count()).isEqualTo(0);

    }

    /**
     * delete Csv without authorization.
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
                .content(csvJson))
                .andExpect(status().isUnauthorized());
    }

    /**
     * list Csvs.
     *
     * @throws Exception Exception
     */
    @WithMockUser(authorities = authority)
    @Test
    void getCsvs() throws Exception {

        // arrange
        getAuthentication(true);

        List<CsvDto> csvDtos = new ArrayList<>();
        csvDtos.add(csvDto);

        String request = objectWriter.writeValueAsString(csvDtos);

        // act and assert
        mvc.perform(post(list))
                .andExpect(status().isOk())
                .andExpect(content().json(request));
    }

    /**
     * list Csvs without authorization.
     *
     * @throws Exception Exception
     */
    @Test
    void getCsvsWithoutAuthorization() throws Exception {

        // arrange
        getAuthentication(true);

        // act and assert
        mvc.perform(post(list))
                .andExpect(status().isUnauthorized());
    }

    /**
     * get records in Csv.
     *
     * @throws Exception Exception
     */
    @Test
    void getRecordDtosInCsv() throws Exception {

        // arrange
        getAuthentication(false);
        List<RecordDto> recordDtos = csvJpaService.getRecordsInCsv(csvDto);
        String result = objectWriter.writeValueAsString(recordDtos);

        // act and assert
        mvc.perform(post(records)
                .contentType(MediaType.APPLICATION_JSON)
                .content(containerJson))
                .andExpect(content().json(result));

    }

    /**
     * get records in Csv without authorization.
     *
     * @throws Exception Exception
     */
    @Test
    void getRecordDtosInCsvWithoutAuthorization() throws Exception {

        // arrange
        getAuthentication(false);
        List<RecordDto> recordDtos = csvJpaService.getRecordsInCsv(csvDto);
        String result = objectWriter.writeValueAsString(recordDtos);

        // act and assert
        mvc.perform(post(records)
                .contentType(MediaType.APPLICATION_JSON)
                .content(containerJson))
                .andExpect(content().json(result));
    }

    /**
     * test save csv.
     */
    @Test
    void saveCsvTest() throws Exception {
        // arrange
        getAuthentication(false);

        assertThat(recordJpaService
                .findById(record.getId()).getLabel()).isEqualTo("testLabel1");
        assertThat(recordJpaService
                .findById(record1.getId()).getLabel()).isEqualTo("testLabel2");

        List<RecordDto> recordDtos = csvJpaService.getRecordsInCsv(csvDto);

        recordDtos.forEach(dto -> {
            dto.setLabel("residual");
        });

        RecordListDto recordListDto = new RecordListDto(recordDtos, container.getId());

        String recordListDtoJson = objectWriter.writeValueAsString(recordListDto);

        // act and assert
        mvc.perform(post(save)
                .contentType(MediaType.APPLICATION_JSON)
                .content(recordListDtoJson))
                .andExpect(status().isOk());

        assertThat(recordJpaService.count()).isEqualTo(2);
        assertThat(recordJpaService
                .findById(record.getId()).getLabel()).isEqualTo("residual");
        assertThat(recordJpaService
                .findById(record1.getId()).getLabel()).isEqualTo("residual");
    }

    @Test
    public void deleteRecordsInCsvTest() throws Exception {
        // arrange
        getAuthentication(false);

        // test deleting existing records
        List<RecordDto> recordDtos = csvJpaService.getRecordsInCsv(csvDto);
        recordDtos.remove(0);
        DeleteDto<RecordDto> deleteDtos = new DeleteDto<>(recordDtos);
        String recordDtosJson = objectWriter.writeValueAsString(deleteDtos);

        // act and assert
        mvc.perform(post(deleteRecords)
                .contentType(MediaType.APPLICATION_JSON)
                .content(recordDtosJson))
                .andExpect(status().isOk());

        assertThat(recordJpaService.count()).isEqualTo(1);
        assertThat(csvJpaService.getRecordsInCsv(csvDto).size()).isEqualTo(1);

        // arrange
        // test deleting non-existent records
        List<RecordDto> nonExistentDtos = new ArrayList<>();
        nonExistentDtos.add(modelMapper.map(record1, RecordDto.class));
        deleteDtos = new DeleteDto<>(nonExistentDtos);
        String nonExistentDtosJson = objectWriter
                .writeValueAsString(deleteDtos);

        // act and assert
        mvc.perform(post(deleteRecords)
                .contentType(MediaType.APPLICATION_JSON)
                .content(nonExistentDtosJson))
                .andExpect(status().isOk());
        assertThat(recordJpaService.count()).isEqualTo(1);
        assertThat(csvJpaService.getRecordsInCsv(csvDto).size()).isEqualTo(1);

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