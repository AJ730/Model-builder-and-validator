package nl.tudelft.sp.modelchecker.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import nl.tudelft.sp.modelchecker.Application;
import nl.tudelft.sp.modelchecker.SecurityFilter.MockSpringSecurityFilter;
import nl.tudelft.sp.modelchecker.dto.ContainerDto;
import nl.tudelft.sp.modelchecker.dto.CsvDto;
import nl.tudelft.sp.modelchecker.dto.RecordDto;
import nl.tudelft.sp.modelchecker.entities.Container;
import nl.tudelft.sp.modelchecker.entities.Csv;
import nl.tudelft.sp.modelchecker.entities.PersistentCsv;
import nl.tudelft.sp.modelchecker.entities.Record;
import nl.tudelft.sp.modelchecker.services.PersistentCsvService;
import nl.tudelft.sp.modelchecker.services.PersistentRecordService;
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
class PersistentCsvControllerTest {

    private final String records = "/api/records/persistentCsv";

    @Autowired
    WebApplicationContext context;
    MockMvc mvc;

    @Autowired
    ModelMapper modelMapper;
    @Autowired
    PersistentCsvService persistentCsvService;
    @Autowired
    PersistentRecordService persistentRecordService;
    @Autowired
    ContainerJpaService containerJpaService;
    @Autowired
    CsvJpaService csvJpaService;
    @Autowired
    RecordJpaService recordJpaService;

    @Autowired
    ObjectWriter objectWriter;
    @Autowired
    ObjectMapper objectMapper;


    private Container container;
    private ContainerDto containerDto;
    private String containerJson;

    private Csv csv;
    private CsvDto csvDto;

    private PersistentCsv persistentCsv;
    private CsvDto persistentCsvDto;

    private MultipartFile classes;

    private Record record;
    private Record record1;

    @BeforeEach
    void setUp() throws Exception {

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

        FileInputStream csvFile =
                new FileInputStream(new File("src/test/resources/detected.csv"));
        MockMultipartFile csvMultipartFile =
                new MockMultipartFile("csv", "detected.csv",
                        "application/octet-stream", csvFile);
        csv = csvJpaService.createCsvAndSaveRecords(containerDto, csvMultipartFile);
        csvDto = new CsvDto(csv);
        persistentCsv = persistentCsvService.saveCsv(containerDto, csvDto);
        persistentCsvDto = new CsvDto(persistentCsv);

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
     * get records in persistent csv test.
     *
     * @throws Exception Exception
     */
    @Test
    void getRecordDtosInPersistentCsv() throws Exception {

        // arrange
        getAuthentication(false);
        assertThat(persistentCsvService.count()).isEqualTo(1);
        assertThat(persistentRecordService.count()).isEqualTo(451);
        assertThat(csvJpaService.count()).isEqualTo(1);
        assertThat(recordJpaService.count()).isEqualTo(453);

        List<RecordDto> recordDtos =
                persistentCsvService.getRecordsInPersistentCsv(persistentCsvDto);

        assertThat(recordDtos.size()).isEqualTo(451);

        String expected = objectWriter.writeValueAsString(recordDtos);

        // act and assert
        mvc.perform(post(records)
                .contentType(MediaType.APPLICATION_JSON)
                .content(containerJson))
                .andExpect(status().isOk())
                .andExpect(content().json(expected));

        assertThat(persistentRecordService.findById(record.getId())).isNull();
        assertThat(persistentRecordService.findById(record1.getId())).isNull();
        assertThat(recordJpaService.count()).isEqualTo(453);
        assertThat(persistentRecordService.count()).isEqualTo(451);
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