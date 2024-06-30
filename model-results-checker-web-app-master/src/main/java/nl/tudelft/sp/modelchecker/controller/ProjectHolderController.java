package nl.tudelft.sp.modelchecker.controller;

import java.util.List;
import javassist.NotFoundException;
import nl.tudelft.sp.modelchecker.dto.ProjectDto;
import nl.tudelft.sp.modelchecker.dto.ProjectHolderDto;
import nl.tudelft.sp.modelchecker.dto.UserDto;
import nl.tudelft.sp.modelchecker.entities.BasicUser;
import nl.tudelft.sp.modelchecker.entities.ProjectHolder;
import nl.tudelft.sp.modelchecker.exceptions.AuthorityException;
import nl.tudelft.sp.modelchecker.services.AuthService;
import nl.tudelft.sp.modelchecker.services.ProjectHolderService;
import nl.tudelft.sp.modelchecker.services.servicebeans.BasicUserJpaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/")
public class ProjectHolderController extends
        AbstractController<Long, ProjectHolderService, ProjectHolder, ProjectHolderDto> {

    @Autowired
    BasicUserJpaService basicUserJpaService;

    @Autowired
    AuthService authService;

    /**
     * Initialize a projectHolder controller.
     *
     * @param service     service
     * @param modelMapper modelMapper
     */
    public ProjectHolderController(ProjectHolderService service,
                                   ModelMapper modelMapper) {
        super(service, modelMapper);
    }


    /**
     * endpoint for admin to delete project holder.
     *
     * @param projectHolderDto project holder dto
     * @return response entity of project holder
     * @throws NotFoundException NotFoundException if project holder does not exist
     */
    @PostMapping("/delete/projectholder")
    @ResponseBody
    @PreAuthorize("hasAuthority('APPROLE_ADMIN')")
    public ResponseEntity<ProjectHolderDto> delete(@RequestBody ProjectHolderDto projectHolderDto)
            throws NotFoundException {
        return super.delete(projectHolderDto);
    }

    /**
     * List all projectHolders.
     *
     * @return projectHolder
     * @throws NotFoundException NotFoundException
     */
    @PostMapping("/list/projectholder")
    @ResponseBody
    @PreAuthorize("hasAuthority('APPROLE_ADMIN')")
    public ResponseEntity<List<ProjectHolderDto>> getProjectHolders()
            throws NotFoundException {
        return super.list(ProjectHolderDto.class);
    }

    /**
     * Get user of a projectHolder.
     *
     * @param userDto userDto
     * @return user
     */
    @PostMapping("/user/projectholder")
    @ResponseBody
    @PreAuthorize("hasAuthority('APPROLE_ADMIN')")
    public ResponseEntity<ProjectHolderDto> getProjectHolder(@RequestBody UserDto userDto) {
        BasicUser basicUser = basicUserJpaService.findById(userDto.getId());
        ProjectHolderDto returnDto = new ProjectHolderDto(basicUser.getProjectHolder());
        return new ResponseEntity<>(returnDto, HttpStatus.OK);
    }


    /**
     * endpoint for get information of project holder.
     *
     * @param projectHolderDto project holder dto
     * @return response entity of project holder
     * @throws NotFoundException NotFoundException
     */
    @PostMapping("/get/projectholder")
    @ResponseBody
    @PreAuthorize("hasAuthority('APPROLE_ADMIN')")
    public ResponseEntity<ProjectHolderDto> getProjectHolder(
            @RequestBody ProjectHolderDto projectHolderDto)
            throws NotFoundException {
        return super.getSpecific(projectHolderDto);
    }

    /**
     * endpoint for retrieving list of projectdtos in project holder.
     *
     * @param projectHolderDto project holder dto
     * @return response entity of list of projectdtos
     * @throws NotFoundException NotFoundException
     */

    @PostMapping("/projects/projectholder")
    @ResponseBody
    public ResponseEntity<List<ProjectDto>> getProjectsInProjectHolder(
            @RequestBody ProjectHolderDto projectHolderDto,
            @RequestHeader("oid") String oid,
            @RequestHeader("Authorization") String bearerToken)
            throws NotFoundException, AuthorityException {

        ProjectHolder projectHolder = service.findById(projectHolderDto.getId());
        authService.validate(bearerToken, oid, projectHolder.getClient().getId());

        List<ProjectDto> projectDtos = service.getProjectDtosInProjectHolder(projectHolderDto);
        return new ResponseEntity<>(projectDtos, HttpStatus.OK);
    }

    /**
     * Get projects of a client.
     *
     * @param userDto     userDto
     * @param oid         oid
     * @param bearerToken bearerToken
     * @return projects
     * @throws NotFoundException  NotFoundException
     * @throws AuthorityException AuthorityException
     */
    @PostMapping("/projects/user/projectholder")
    @ResponseBody
    public ResponseEntity<List<ProjectDto>> getProjectsClientInProjectHolder(
            @RequestBody UserDto userDto,
            @RequestHeader("oid") String oid,
            @RequestHeader("Authorization") String bearerToken)
            throws NotFoundException, AuthorityException {

        String userId = userDto.getId();
        authService.validate(bearerToken, oid, userId);

        BasicUser basicUser = basicUserJpaService.findById(userId);
        List<ProjectDto> projectDtos = service.getProjectDtosInProjectHolder(
                new ProjectHolderDto(basicUser.getProjectHolder()));

        return new ResponseEntity<>(projectDtos, HttpStatus.OK);
    }
}
