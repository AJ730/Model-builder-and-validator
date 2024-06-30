package nl.tudelft.sp.modelchecker.services;

import java.util.List;
import javassist.NotFoundException;
import javax.validation.constraints.NotNull;
import nl.tudelft.sp.modelchecker.dto.ContainerDto;
import nl.tudelft.sp.modelchecker.dto.ProjectDto;
import nl.tudelft.sp.modelchecker.dto.ProjectHolderDto;
import nl.tudelft.sp.modelchecker.dto.UserDto;
import nl.tudelft.sp.modelchecker.entities.Project;
import nl.tudelft.sp.modelchecker.exceptions.ExistsException;

public interface ProjectService extends CrudService<Project, Long, ProjectDto> {

    /**
     * Register a project.
     *
     * @param project          project
     * @param projectHolderDto projectHolderDto
     * @param userDto          userDto
     * @return registered project
     * @throws ExistsException   ExistsException
     * @throws NotFoundException NotFoundException
     */
    Project register(@NotNull Project project, @NotNull ProjectHolderDto projectHolderDto,
                     @NotNull UserDto userDto) throws ExistsException, NotFoundException;


    /**
     * Get containerDtos in a project.
     *
     * @param projectDto projectDto
     * @return containerDto
     * @throws NotFoundException NotFoundException
     */
    List<ContainerDto> getContainerDtosInProject(ProjectDto projectDto)
            throws NotFoundException;

    /**
     * Change projectHolder in a project.
     *
     * @param projectDto projectDto
     * @return changed project
     * @throws NotFoundException NotFoundException
     */
    Project changeProjectHolder(ProjectDto projectDto) throws NotFoundException;

}
