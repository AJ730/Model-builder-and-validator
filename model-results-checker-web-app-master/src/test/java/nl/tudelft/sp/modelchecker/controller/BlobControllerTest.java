package nl.tudelft.sp.modelchecker.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sp.modelchecker.Application;
import nl.tudelft.sp.modelchecker.SecurityFilter.MockSpringSecurityFilter;
import nl.tudelft.sp.modelchecker.cloud.Connection;
import nl.tudelft.sp.modelchecker.dto.BlobDto;
import nl.tudelft.sp.modelchecker.dto.BlobListDto;
import nl.tudelft.sp.modelchecker.dto.ContainerDto;
import nl.tudelft.sp.modelchecker.entities.Container;
import nl.tudelft.sp.modelchecker.exceptions.ExistsException;
import nl.tudelft.sp.modelchecker.services.ContainerService;
import nl.tudelft.sp.modelchecker.services.ProjectService;
import nl.tudelft.sp.modelchecker.services.servicebeans.AzureBlobJpaService;
import nl.tudelft.sp.modelchecker.services.servicebeans.AzureContainerJpaService;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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
class BlobControllerTest {

    private final String getBlob = "/api/get/blob";
    private final String getBlobList = "/api/get/blobList";

    @Autowired
    WebApplicationContext context;
    MockMvc mvc;
    @Autowired
    ContainerService containerService;
    @Autowired
    ProjectService projectService;
    @Autowired
    Connection connection;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    ObjectWriter objectWriter;
    @Autowired
    BlobController blobController;
    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AzureContainerJpaService azureContainerJpaService;

    @MockBean
    AzureBlobJpaService azureBlobJpaService;

    Container container;
    ContainerDto containerDto;

    BlobListDto blobListDto;
    BlobDto blobDto;
    String blobName = "blobName";
    String expected;
    String containerDtoJson;


    @BeforeEach
    void setUp() throws ExistsException, JsonProcessingException {
        container = new Container(23.97, "desc", "name");
        container.setBlobName(blobName);
        container = containerService.register(container);
        containerDto = new ContainerDto(container);
        containerDtoJson = objectWriter.writeValueAsString(containerDto);
    }

    /**
     * get blob names in cloud container test.
     *
     * @throws Exception Exception
     */
    @Test
    void getBlobNames() throws Exception {
        getAuthentication(false);
        List<String> names =
            azureContainerJpaService
                .listBlobs(connection.getDefaultContainer());
        blobListDto = new BlobListDto(names);
        expected = objectWriter.writeValueAsString(blobListDto);

        mvc.perform(post(getBlobList))
            .andExpect(status().isOk())
            .andExpect(content().json(expected));
    }

    /**
     * get the URI of the blob in the container test.
     *
     * @throws Exception Exception
     */
    @Test
    void getBlobUri() throws Exception {
        getAuthentication(false);
        URI uri = URI.create("https://recycleye.com");
        blobDto = new BlobDto(uri);
        expected = objectWriter.writeValueAsString(blobDto);
        when(azureBlobJpaService
            .generateSasLink(connection.getDefaultContainer(), blobName))
            .thenReturn(uri);
        mvc.perform(post(getBlob)
            .contentType(MediaType.APPLICATION_JSON)
            .content(containerDtoJson))
            .andExpect(status().isOk())
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