package nl.tudelft.sp.modelchecker.services.servicebeans;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javassist.NotFoundException;
import javax.validation.constraints.NotNull;
import nl.tudelft.sp.modelchecker.dto.ProjectDto;
import nl.tudelft.sp.modelchecker.dto.ProjectHolderDto;
import nl.tudelft.sp.modelchecker.dto.UserDto;
import nl.tudelft.sp.modelchecker.entities.BasicUser;
import nl.tudelft.sp.modelchecker.entities.Project;
import nl.tudelft.sp.modelchecker.entities.ProjectHolder;
import nl.tudelft.sp.modelchecker.exceptions.AuthorityException;
import nl.tudelft.sp.modelchecker.exceptions.ExistsException;
import nl.tudelft.sp.modelchecker.services.ProjectHolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProjectHolderJpaService extends CrudJpaService<ProjectHolder, Long, ProjectHolderDto>
        implements ProjectHolderService {

    @Autowired
    private BasicUserJpaService basicUserJpaService;

    @Autowired
    private AdminJpaService adminJpaService;


    /**
     * Initialize a ProjectHolderRepository.
     *
     * @param repository repository
     */
    public ProjectHolderJpaService(JpaRepository<ProjectHolder, Long> repository) {
        super(repository);
    }


    /**
     * Register a projectHolder.
     *
     * @param projectHolder projectHolder
     * @param client        client
     * @return registered ProjectHolder.
     * @throws ExistsException    ExistsException
     * @throws NotFoundException  NotFoundException
     * @throws AuthorityException AuthorityException
     */
    @Transactional(rollbackFor = {Exception.class},
            propagation = Propagation.REQUIRED)
    @Override
    public ProjectHolder register(@NotNull ProjectHolder projectHolder,
                                  @NotNull UserDto client)
            throws ExistsException, NotFoundException, AuthorityException {

        if (exists(projectHolder)) {
            throw new ExistsException("ProjectHolder does not exist");
        }
        validate(client);

        BasicUser currentUser = basicUserJpaService.findById(client.getId());

        currentUser.setProjectHolder(projectHolder);
        projectHolder.setClient(currentUser);

        return save(projectHolder);
    }


    /**
     * Update a projectHolder(Not Supported).
     *
     * @param oldDto oldDto
     * @param newDto newDto
     * @return updated Dto.
     */
    @Transactional(rollbackFor = {Exception.class},
            propagation = Propagation.REQUIRED)
    @Override
    public ProjectHolder update(ProjectHolderDto oldDto, ProjectHolderDto newDto) {
        throw new UnsupportedOperationException("ProjectHolders cannot be updated");
    }

    /**
     * Get Projects from a projectHolder.
     *
     * @param projectHolderDto projectHolderDto
     * @return List of ProjectDtos
     */
    @Transactional(rollbackFor = {Exception.class},
            propagation = Propagation.REQUIRED)
    @Override
    public List<ProjectDto> getProjectDtosInProjectHolder(
            @NotNull ProjectHolderDto projectHolderDto) throws NotFoundException {

        ProjectHolder projectHolder = findById(projectHolderDto.getId());
        if (projectHolder == null) throw new NotFoundException("ProjectHolder not found");

        Set<Project> projects = projectHolder.getProjects();
        return projects.stream().map(ProjectDto::new)
                .collect(Collectors.toList());
    }


    /**
     * Validate a user.
     *
     * @param client client
     * @throws NotFoundException  NotFoundException
     * @throws AuthorityException AuthorityException
     */
    //TODO: needs to be replaced with chain of responsibility pattern
    private void validate(UserDto client)
            throws NotFoundException, AuthorityException {

        if (!basicUserJpaService.exists(client)) {
            throw new NotFoundException("User does not exist");
        }
        if (adminJpaService.isAdmin(client)) {
            throw new AuthorityException("User cannot be admin");
        }
    }


}
