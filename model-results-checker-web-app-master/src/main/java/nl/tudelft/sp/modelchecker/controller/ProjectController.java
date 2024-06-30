package nl.tudelft.sp.modelchecker.controller;

import java.util.List;
import javassist.NotFoundException;
import nl.tudelft.sp.modelchecker.dto.ContainerDto;
import nl.tudelft.sp.modelchecker.dto.ProjectDto;
import nl.tudelft.sp.modelchecker.dto.ProjectHolderDto;
import nl.tudelft.sp.modelchecker.dto.UserDto;
import nl.tudelft.sp.modelchecker.entities.Admin;
import nl.tudelft.sp.modelchecker.entities.Project;
import nl.tudelft.sp.modelchecker.entities.ProjectHolder;
import nl.tudelft.sp.modelchecker.exceptions.AuthorityException;
import nl.tudelft.sp.modelchecker.exceptions.ExistsException;
import nl.tudelft.sp.modelchecker.services.AdminService;
import nl.tudelft.sp.modelchecker.services.AuthService;
import nl.tudelft.sp.modelchecker.services.ProjectHolderService;
import nl.tudelft.sp.modelchecker.services.ProjectService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
public class ProjectController
        extends AbstractController<Long, ProjectService, Project, ProjectDto> {

    @Autowired
    private ProjectHolderService projectHolderService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private AuthService authService;

    /**
     * Initialize a project controller.
     *
     * @param service     service
     * @param modelMapper modelMapper
     */
    public ProjectController(ProjectService service, ModelMapper modelMapper) {
        super(service, modelMapper);
    }

    /**
     * Create a project.
     *
     * @param projectDto projectDto
     * @return created project
     * @throws ExistsException   ExistsException
     * @throws NotFoundException NotFoundException
     */
    @PostMapping("/create/project")
    @ResponseBody
    @PreAuthorize("hasAuthority('APPROLE_ADMIN')")
    public ResponseEntity<ProjectDto> create(@RequestBody ProjectDto projectDto)
            throws ExistsException, NotFoundException {

        ProjectHolder projectHolder = projectHolderService
                .findById(projectDto.getProjectHolderId());
        Project project = modelMapper.map(projectDto, Project.class);

        Admin admin = adminService.findById(projectDto.getAdminId());

        ProjectHolderDto projectHolderDto = new ProjectHolderDto(projectHolder);
        project = service.register(project, projectHolderDto, new UserDto(admin));

        return new ResponseEntity<>(new ProjectDto(project), HttpStatus.OK);
    }

    /**
     * Get a project.
     *
     * @param projectDto  projectDto
     * @param oid         oid
     * @param bearerToken bearerToken
     * @return specific project
     * @throws NotFoundException  NotFoundException
     * @throws AuthorityException AuthorityException
     */
    @PostMapping("/get/project")
    @ResponseBody
    public ResponseEntity<ProjectDto> get(@RequestBody ProjectDto projectDto,
                                          @RequestHeader("oid") String oid,
                                          @RequestHeader("Authorization") String bearerToken)
            throws NotFoundException, AuthorityException {

        Project project = service.findById(projectDto.getId());
        String userId = project.getProjectHolder().getClient().getId();
        authService.validate(bearerToken, oid, userId);

        return super.getSpecific(projectDto);
    }

    /**
     * Update a project.
     *
     * @param projectDto projectDto
     * @return updated project
     * @throws NotFoundException NotFoundException
     */
    @PostMapping("/update/project")
    @ResponseBody
    @PreAuthorize("hasAuthority('APPROLE_ADMIN')")
    public ResponseEntity<ProjectDto> update(@RequestBody ProjectDto projectDto)
            throws NotFoundException {
        return super.update(projectDto);
    }

    /**
     * Delete a project.
     *
     * @param projectDto projectDto
     * @return deleted project
     * @throws NotFoundException NotFoundException
     */
    @PostMapping("/delete/project")
    @ResponseBody
    @PreAuthorize("hasAuthority('APPROLE_ADMIN')")
    public ResponseEntity<ProjectDto> delete(@RequestBody ProjectDto projectDto)
            throws NotFoundException {
        return super.delete(projectDto);
    }

    /**
     * Reassign a project.
     *
     * @param projectDto projectDto
     * @return reassign project
     * @throws NotFoundException NotFoundException
     */
    @PostMapping("/reassign/project")
    @PreAuthorize("hasAuthority('APPROLE_ADMIN')")
    public ResponseEntity<ProjectDto> changeProject(@RequestBody ProjectDto projectDto)
            throws NotFoundException {
        Project project = service.changeProjectHolder(projectDto);
        return new ResponseEntity<>(new ProjectDto(project), HttpStatus.OK);
    }

    /**
     * List all projects.
     *
     * @return listed projects.
     * @throws NotFoundException NotFoundException
     */
    @PostMapping("/list/project")
    @ResponseBody
    @PreAuthorize("hasAuthority('APPROLE_ADMIN')")
    public ResponseEntity<List<ProjectDto>> getProjects()
            throws NotFoundException {
        return super.list(ProjectDto.class);
    }

    /**
     * Get clients of a project.
     *
     * @param projectDto projectDto
     * @return clients
     */
    @PostMapping("/getclient/project")
    @ResponseBody
    @PreAuthorize("hasAuthority('APPROLE_ADMIN')")
    public ResponseEntity<UserDto> getClient(@RequestBody ProjectDto projectDto) {
        Project project = service.findById(projectDto.getId());
        UserDto returnDto = new UserDto(project.getProjectHolder().getClient());
        return new ResponseEntity<>(returnDto, HttpStatus.OK);
    }

    /**
     * Get containers of a project.
     *
     * @param projectDto  projectDto
     * @param oid         oid
     * @param bearerToken bearerToken
     * @return containers in a project
     * @throws NotFoundException  NotFoundException
     * @throws AuthorityException AuthorityException
     */
    @PostMapping("/containers/project")
    @ResponseBody
    public ResponseEntity<List<ContainerDto>> getContainerInProject(
            @RequestBody ProjectDto projectDto, @RequestHeader("oid") String oid,
            @RequestHeader("Authorization") String bearerToken)
            throws NotFoundException, AuthorityException {

        Project project = service.findById(projectDto.getId());
        String userId = project.getProjectHolder().getClient().getId();
        authService.validate(bearerToken, oid, userId);

        List<ContainerDto> containerDtos = service.getContainerDtosInProject(projectDto);
        return new ResponseEntity<>(containerDtos, HttpStatus.OK);
    }
}
