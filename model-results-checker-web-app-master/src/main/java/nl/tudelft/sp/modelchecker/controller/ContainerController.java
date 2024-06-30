package nl.tudelft.sp.modelchecker.controller;

import com.microsoft.azure.storage.StorageException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import javassist.NotFoundException;
import lombok.Setter;
import nl.tudelft.sp.modelchecker.cloud.Connection;
import nl.tudelft.sp.modelchecker.dto.BlobDto;
import nl.tudelft.sp.modelchecker.dto.BlobListDto;
import nl.tudelft.sp.modelchecker.dto.ContainerDto;
import nl.tudelft.sp.modelchecker.dto.ProjectDto;
import nl.tudelft.sp.modelchecker.entities.Container;
import nl.tudelft.sp.modelchecker.entities.Project;
import nl.tudelft.sp.modelchecker.services.*;
import nl.tudelft.sp.modelchecker.videoprocessing.VideoProcessor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/")
@Setter
public class ContainerController
        extends AbstractController<Long, ContainerService, Container, ContainerDto> {

    @Autowired
    ProjectService projectService;

    @Autowired
    ContainerService containerService;

    @Autowired
    AzureBlobService azureBlobService;

    @Autowired
    Connection connection;

    @Autowired
    CsvService csvService;

    @Autowired
    RecordService recordService;

    @Autowired
    AzureContainerService azureContainerService;

    @Setter
    VideoProcessor videoProcessor;

    /**
     * Initiialize a container controller.
     *
     * @param service     service
     * @param modelMapper modelMapper
     */
    public ContainerController(ContainerService service, ModelMapper modelMapper) {
        super(service, modelMapper);
    }


    /**
     * create container endpoint.
     *
     * @param blobName blobName
     * @param csv csv
     * @param classes classes
     * @param projectId projectId
     * @param description description
     * @param name name
     * @return response entity
     * @throws Exception Exception
     */
    @PostMapping("/create/container")
    @ResponseBody
    @PreAuthorize("hasAuthority('APPROLE_ADMIN')")
    public ResponseEntity<Void> create(
            @RequestParam("blobName") String blobName,
            @RequestParam("csv") MultipartFile csv,
            @RequestParam("classes") MultipartFile classes,
            @RequestParam Long projectId,
            @RequestParam String description,
            @RequestParam String name) throws Exception {

        Project project = projectService.findById(projectId);
        ProjectDto projectDto = modelMapper.map(project, ProjectDto.class);
        Double frameRate = azureBlobService.getFps(blobName, connection.getDefaultContainer());

        Container container = Container.builder()
                .frameRate(frameRate)
                .name(name)
                .blobName(blobName)
                .description(description)
                .csvName(csv.getOriginalFilename())
                .className(classes.getOriginalFilename())
                .build();

        containerService.fillContainer(csv, classes, container, projectDto);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Update a container.
     *
     * @param containerDto containerDto
     * @return updated container
     * @throws NotFoundException NotFoundException
     */
    @PostMapping("/update/container")
    @ResponseBody
    @PreAuthorize("hasAuthority('APPROLE_ADMIN')")
    public ResponseEntity<ContainerDto> update(@RequestBody ContainerDto containerDto)
            throws NotFoundException {
        return super.update(containerDto);
    }

    /**
     * Get a proxy of a container.
     *
     * @param containerDto containerDto
     * @return proxy
     * @throws NotFoundException NotFoundException
     */
    @PostMapping("/get/container")
    @ResponseBody
    public ResponseEntity<ContainerDto> get(@RequestBody ContainerDto containerDto)
            throws NotFoundException {
        return super.getSpecific(containerDto);
    }

    /**
     * Delete a container.
     *
     * @param containerDto containerDto
     * @return deleted container
     * @throws NotFoundException NotFoundException
     */
    @PostMapping("/delete/container")
    @ResponseBody
    @PreAuthorize("hasAuthority('APPROLE_ADMIN')")
    public ResponseEntity<ContainerDto> delete(@RequestBody ContainerDto containerDto)
            throws NotFoundException {
        return super.delete(containerDto);
    }

    /**
     * List all the containers.
     *
     * @return all containers
     * @throws NotFoundException NotFoundException
     */
    @PostMapping("/list/container")
    @ResponseBody
    @PreAuthorize("hasAuthority('APPROLE_ADMIN')")
    public ResponseEntity<List<ContainerDto>> getContainers() throws NotFoundException {
        return super.list(ContainerDto.class);
    }

    /**
     * Get all classes.
     *
     * @param containerDto containerDto
     * @return classes
     */
    @PostMapping("/get/classes")
    @ResponseBody
    public ResponseEntity<List<String>> getClasses(@RequestBody ContainerDto containerDto) {
        Container container = service.findById(containerDto.getId());
        return new ResponseEntity<>(container.getClasses(), HttpStatus.OK);
    }

}
