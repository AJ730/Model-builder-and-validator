package nl.tudelft.sp.modelchecker.services;

import java.io.IOException;
import javassist.NotFoundException;
import nl.tudelft.sp.modelchecker.dto.ContainerDto;
import nl.tudelft.sp.modelchecker.dto.RecordListDto;
import nl.tudelft.sp.modelchecker.dto.SubmissionDto;
import nl.tudelft.sp.modelchecker.dto.UserDto;
import nl.tudelft.sp.modelchecker.entities.Submission;
import nl.tudelft.sp.modelchecker.exceptions.AuthorityException;
import nl.tudelft.sp.modelchecker.exceptions.ExistsException;

public interface SubmissionService extends CrudService<Submission, Long, SubmissionDto> {

    /**
     * Register a submission with a client.
     *
     * @param submission submission
     * @param client     client
     * @return Submission
     * @throws ExistsException    ExistsException
     * @throws NotFoundException  NotFoundException
     * @throws AuthorityException AuthorityException
     */
    Submission register(Submission submission, UserDto client)
            throws ExistsException, NotFoundException, AuthorityException;

    /**
     * register submission.
     *
     * @param recordListDto recordListDto
     * @param clientId      clientId
     * @throws ExistsException    ExistsException
     * @throws NotFoundException  NotFoundException
     * @throws IOException        IOException
     * @throws AuthorityException AuthorityException
     */
    void register(RecordListDto recordListDto, String clientId)
            throws ExistsException, NotFoundException, IOException, AuthorityException;

    /**
     * Assign a container to a submission.
     *
     * @param submission   submission
     * @param containerDto containerDto
     * @return Submission
     * @throws NotFoundException NotFoundException
     */
    Submission assignContainer(Submission submission, ContainerDto containerDto)
            throws NotFoundException;


}
