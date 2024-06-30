package nl.tudelft.sp.modelchecker.services;

import java.io.IOException;
import javassist.NotFoundException;
import nl.tudelft.sp.modelchecker.dto.ContainerDto;
import nl.tudelft.sp.modelchecker.dto.ProjectDto;
import nl.tudelft.sp.modelchecker.dto.RecordListDto;
import nl.tudelft.sp.modelchecker.entities.Container;
import nl.tudelft.sp.modelchecker.exceptions.ExistsException;
import org.springframework.web.multipart.MultipartFile;

public interface ContainerService extends CrudService<Container, Long, ContainerDto> {

    /**
     * Register a container with a project.
     *
     * @param container  container
     * @param projectDto projectDto
     * @return a new container
     * @throws ExistsException   ExistsException
     * @throws NotFoundException NotFoundException
     */
    Container register(Container container, ProjectDto projectDto)
            throws ExistsException, NotFoundException;

    /**
     * Fill container.
     *
     * @param csv        csv
     * @param classes    classes
     * @param container  container
     * @param projectDto projectDto
     * @return filled container
     * @throws Exception Exception
     */
    Container fillContainer(MultipartFile csv,
                            MultipartFile classes, Container container,
                            ProjectDto projectDto)
            throws Exception;

    /**
     * Register classes.
     *
     * @param classes      classes
     * @param containerDto containerDto
     * @throws IOException IOException
     */
    void registerClasses(MultipartFile classes, ContainerDto containerDto)
            throws IOException;


    /**
     * Overide container.
     *
     * @param recordListDto recordListDto
     * @return container
     * @throws ExistsException   ExistsException
     * @throws NotFoundException NotFoundException
     * @throws IOException       IOException
     */
    Container overideContainer(RecordListDto recordListDto)
            throws ExistsException, NotFoundException, IOException;
}
