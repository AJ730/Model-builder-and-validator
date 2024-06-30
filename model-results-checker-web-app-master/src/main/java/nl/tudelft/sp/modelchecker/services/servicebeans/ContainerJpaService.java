package nl.tudelft.sp.modelchecker.services.servicebeans;

import java.io.IOException;
import java.util.List;
import javassist.NotFoundException;
import nl.tudelft.sp.modelchecker.dto.*;
import nl.tudelft.sp.modelchecker.entities.Container;
import nl.tudelft.sp.modelchecker.entities.Csv;
import nl.tudelft.sp.modelchecker.entities.Project;
import nl.tudelft.sp.modelchecker.exceptions.ExistsException;
import nl.tudelft.sp.modelchecker.parsers.FileParser.Adapter;
import nl.tudelft.sp.modelchecker.services.ContainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
public class ContainerJpaService extends CrudJpaService<Container, Long, ContainerDto>
        implements ContainerService {

    @Autowired
    private ProjectJpaService projectJpaService;

    @Autowired
    private CsvJpaService csvJpaService;

    @Autowired
    private RecordJpaService recordJpaService;

    @Autowired
    private PersistentCsvJpaService persistentCsvJpaService;


    /**
     * Constructor for containerService.
     *
     * @param repository repository
     */
    public ContainerJpaService(JpaRepository<Container, Long> repository) {
        super(repository);
    }


    /**
     * Register a new container with a projectDto.
     *
     * @param container  container
     * @param projectDto projectDto
     * @return registered Container
     * @throws ExistsException   ExistsException
     * @throws NotFoundException NotFoundException
     */
    @Transactional(rollbackFor = {Exception.class},
            propagation = Propagation.REQUIRED)
    @Override
    public Container register(Container container, ProjectDto projectDto)
            throws ExistsException, NotFoundException {

        if (exists(container)) {
            throw new ExistsException("Container already exists");
        }

        if (!projectJpaService.exists(projectDto)) {
            throw new NotFoundException("Project not found");
        }

        Project project = projectJpaService.findById(projectDto.getId());

        project.getContainers().add(container);
        container.setProject(project);


        return save(container);
    }

    /**
     * Update a container.
     *
     * @param oldDto oldDto
     * @param newDto newDto
     * @return updated Container
     * @throws NotFoundException NotFoundException
     */
    @Transactional(rollbackFor = {Exception.class},
            propagation = Propagation.REQUIRED)
    @Override
    public Container update(ContainerDto oldDto, ContainerDto newDto) throws NotFoundException {

        if (!exists(oldDto)) throw new NotFoundException("ContainerDto not found");

        Container container = findById(oldDto.getId());
        container.setFrameRate(newDto.getFrameRate());
        container.setDescription(newDto.getDescription());
        container.setName(newDto.getName());

        return save(container);
    }

    /**
     * Fill a container with given params.
     *
     * @param multipartFileCsv multipartFileCsv
     * @param classes          classes
     * @param container        container
     * @param projectDto       projectDto
     * @throws Exception Exception
     */
    //TODO change this to use fascade pattern
    @Transactional(rollbackFor = {Exception.class},
            propagation = Propagation.REQUIRED)
    @Override
    public Container fillContainer(MultipartFile multipartFileCsv,
                                   MultipartFile classes, Container container,
                                   ProjectDto projectDto)
            throws Exception {

        Container processContainer = register(container, projectDto);
        ContainerDto containerDto = new ContainerDto(processContainer);

        registerClasses(classes, containerDto);

        Csv csv = csvJpaService
                .createCsvAndSaveRecords(containerDto, multipartFileCsv);

        persistentCsvJpaService.saveCsv(containerDto, new CsvDto(csv));

        return processContainer;
    }


    /**
     * Overide the container.
     *
     * @param recordListDto recordListDto
     * @return container
     * @throws ExistsException   ExistsException
     * @throws NotFoundException NotFoundException
     * @throws IOException       IOException
     */
    @Transactional(rollbackFor = {Exception.class},
            propagation = Propagation.REQUIRED)
    @Override
    public Container overideContainer(RecordListDto recordListDto)
            throws ExistsException, NotFoundException, IOException {

        List<RecordDto> recordDtos = recordListDto.getRecordDtos();
        Container container = findById(recordListDto.getContainerId());
        Csv returnCsv = csvJpaService.findById(container.getCsv().getId());
        recordJpaService.save(recordDtos, new CsvDto(returnCsv));

        return container;
    }


    /**
     * Register classes.
     *
     * @param classes      classes
     * @param containerDto containerDto
     * @throws IOException IOException
     */
    @Transactional(rollbackFor = {Exception.class},
            propagation = Propagation.REQUIRED)
    @Override
    public void registerClasses(MultipartFile classes, ContainerDto containerDto)
            throws IOException {
        Container container = findById(containerDto.getId());
        container.setClasses(getClasses(classes));
        save(container);
    }

    /**
     * Get classes of a multipart file.
     *
     * @param multipartFile multipartFile
     * @return classes
     * @throws IOException IOException
     */
    private List<String> getClasses(MultipartFile multipartFile) throws IOException {
        Adapter adapter = new Adapter(multipartFile);
        return adapter.getClasses(multipartFile);
    }
}
