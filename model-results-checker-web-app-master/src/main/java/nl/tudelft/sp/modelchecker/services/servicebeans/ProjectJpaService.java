package nl.tudelft.sp.modelchecker.services.servicebeans;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javassist.NotFoundException;
import javax.validation.constraints.NotNull;
import nl.tudelft.sp.modelchecker.dto.ContainerDto;
import nl.tudelft.sp.modelchecker.dto.ProjectDto;
import nl.tudelft.sp.modelchecker.dto.ProjectHolderDto;
import nl.tudelft.sp.modelchecker.dto.UserDto;
import nl.tudelft.sp.modelchecker.entities.Admin;
import nl.tudelft.sp.modelchecker.entities.Container;
import nl.tudelft.sp.modelchecker.entities.Project;
import nl.tudelft.sp.modelchecker.entities.ProjectHolder;
import nl.tudelft.sp.modelchecker.exceptions.ExistsException;
import nl.tudelft.sp.modelchecker.repositories.ProjectRepository;
import nl.tudelft.sp.modelchecker.services.ProjectService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
public class ProjectJpaService extends CrudJpaService<Project, Long, ProjectDto>
        implements ProjectService {

    @Autowired
    private ProjectHolderJpaService projectHolderJpaService;

    @Autowired
    private AdminJpaService adminJpaService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Initialize a project repository.
     *
     * @param repository repository
     */
    public ProjectJpaService(JpaRepository<Project, Long> repository) {
        super(repository);
    }

    /**
     * Register a project with projectHolder.
     *
     * @param project          project
     * @param projectHolderDto projectHolderDto
     * @return projectHolder
     * @throws ExistsException   ExistsException
     * @throws NotFoundException NotFoundException
     */
    @Transactional(rollbackFor = {Exception.class},
            propagation = Propagation.REQUIRED)
    @Override
    public Project register(@NotNull Project project, @NotNull ProjectHolderDto projectHolderDto,
                            @NotNull UserDto userDto)

            throws ExistsException, NotFoundException {

        if (exists(project)) {
            throw new ExistsException("Project already exists");
        }

        if (!projectHolderJpaService.exists(projectHolderDto)) {
            throw new NotFoundException("ProjectHolder does not exist");
        }

        Admin admin = adminJpaService.findById(userDto.getId());

        if (admin == null) {
            throw new NotFoundException("Admin does not exist");
        }

        admin.getProjects().add(project);
        project.setAdmin(admin);

        ProjectHolder projectHolder = projectHolderJpaService.findById(projectHolderDto.getId());
        projectHolder.getProjects().add(project);
        project.setProjectHolder(projectHolder);

        return save(project);
    }

    /**
     * Update project.
     *
     * @param oldDto oldDto
     * @param newDto newDto
     * @return updated project
     * @throws NotFoundException NotFoundException
     */
    @Transactional(rollbackFor = {Exception.class},
            propagation = Propagation.REQUIRED)
    @Override
    public Project update(@NotNull ProjectDto oldDto, @NotNull ProjectDto newDto)
            throws NotFoundException {

        if (!exists(oldDto)) throw new NotFoundException("ProjectDto not found");

        Project project = findById(oldDto.getId());
        if (newDto.getTitle() != null) {
            project.setTitle(newDto.getTitle());
        }
        if (newDto.getDescription() != null) {
            project.setDescription(newDto.getDescription());
        }

        return save(project);
    }


    @Transactional(rollbackFor = {Exception.class},
            propagation = Propagation.REQUIRED)
    @Override
    public List<ContainerDto> getContainerDtosInProject(ProjectDto projectDto)
            throws NotFoundException {

        Project project = findById(projectDto.getId());
        if (project == null) throw new NotFoundException("Project not found");

        Set<Container> containers = project.getContainers();
        return containers.stream().map(ContainerDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = {Exception.class},
            propagation = Propagation.REQUIRED)
    @Override
    public Project changeProjectHolder(ProjectDto projectDto)
            throws NotFoundException {

        Project project = findById(projectDto.getId());
        ProjectHolder projectHolder = project.getProjectHolder();


        if (projectHolder == null) {
            throw new NotFoundException("Project Holder not found");
        }

        Set<Project> projects = new LinkedHashSet<>(projectHolder.getProjects()) {
        };
        projects.remove(project);
        projectHolder.setProjects(projects);

        ProjectHolder newHolder = projectHolderJpaService
                .findById(projectDto.getProjectHolderId());

        if (newHolder == null) {
            throw new NotFoundException("Target Project Holder not found");
        }

        project.setProjectHolder(newHolder);
        newHolder.getProjects().add(project);

        return save(project);
    }
}

