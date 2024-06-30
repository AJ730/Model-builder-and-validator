package nl.tudelft.sp.modelchecker.services;

import java.util.List;
import javassist.NotFoundException;
import nl.tudelft.sp.modelchecker.dto.ProjectDto;
import nl.tudelft.sp.modelchecker.dto.ProjectHolderDto;
import nl.tudelft.sp.modelchecker.dto.UserDto;
import nl.tudelft.sp.modelchecker.entities.ProjectHolder;
import nl.tudelft.sp.modelchecker.exceptions.AuthorityException;
import nl.tudelft.sp.modelchecker.exceptions.ExistsException;

public interface ProjectHolderService extends CrudService<ProjectHolder, Long, ProjectHolderDto> {

    /**
     * Register ProjectHolder with client.
     *
     * @param projectHolder projectHolder
     * @param client        client
     * @return ProjectHolder
     * @throws ExistsException    ExistsException
     * @throws NotFoundException  NotFoundException
     * @throws AuthorityException AuthorityException
     */
    ProjectHolder register(ProjectHolder projectHolder, UserDto client)
            throws ExistsException, NotFoundException, AuthorityException;


    /**
     * Get Projects from a projectHolder.
     *
     * @param projectHolderDto projectHolderDto
     * @return List of ProjectDtos
     * @throws NotFoundException NotFoundException
     */
    List<ProjectDto> getProjectDtosInProjectHolder(ProjectHolderDto projectHolderDto)
            throws NotFoundException;

}
