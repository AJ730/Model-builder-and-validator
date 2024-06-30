package nl.tudelft.sp.modelchecker.services.servicebeans;

import java.io.IOException;
import javassist.NotFoundException;
import javax.validation.constraints.NotNull;
import nl.tudelft.sp.modelchecker.dto.ContainerDto;
import nl.tudelft.sp.modelchecker.dto.RecordListDto;
import nl.tudelft.sp.modelchecker.dto.SubmissionDto;
import nl.tudelft.sp.modelchecker.dto.UserDto;
import nl.tudelft.sp.modelchecker.entities.BasicUser;
import nl.tudelft.sp.modelchecker.entities.Container;
import nl.tudelft.sp.modelchecker.entities.Submission;
import nl.tudelft.sp.modelchecker.exceptions.AuthorityException;
import nl.tudelft.sp.modelchecker.exceptions.ExistsException;
import nl.tudelft.sp.modelchecker.services.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SubmissionJpaService extends CrudJpaService<Submission, Long, SubmissionDto>
        implements SubmissionService {

    @Autowired
    private BasicUserJpaService basicUserJpaService;

    @Autowired
    private AdminJpaService adminJpaService;

    @Autowired
    private ContainerJpaService containerJpaService;

    /**
     * Constructor for submissionJpaService.
     *
     * @param repository repository
     */
    public SubmissionJpaService(JpaRepository<Submission, Long> repository) {
        super(repository);
    }

    @Transactional(rollbackFor = {Exception.class,
        NotFoundException.class,
        AuthorityException.class},
        propagation = Propagation.REQUIRED)
    @Override
    public Submission register(@NotNull Submission submission,
                               @NotNull UserDto client) throws ExistsException,
            NotFoundException, AuthorityException {

        if (exists(submission)) {
            throw new ExistsException("Submission already exist");
        }

        if (!basicUserJpaService.exists(client)) {
            throw new NotFoundException("User does not exist");
        }

        if (adminJpaService.isAdmin(client)) {
            throw new AuthorityException("Admin cannot register a submission");
        }

        BasicUser basicUser = basicUserJpaService.findById(client.getId());
        basicUser.setSubmission(submission);
        submission.setClient(basicUser);

        return save(submission);
    }

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
    @Transactional(rollbackFor = {Exception.class, NotFoundException.class,
        AuthorityException.class},
        propagation = Propagation.REQUIRED)
    public void register(RecordListDto recordListDto, String clientId)
            throws ExistsException, NotFoundException, IOException, AuthorityException {
        Long containerId = recordListDto.getContainerId();
        Container container = containerJpaService.findById(recordListDto.getContainerId());
        if (container.getSubmission() != null) {
            deleteById(container.getSubmission().getId());
        }
        Submission submission = register(new Submission(), new UserDto(clientId));
        containerJpaService.overideContainer(recordListDto);
        assignContainer(submission, new ContainerDto(containerId));
    }


    /**
     * Assign a submission to a container.
     *
     * @param submission   submission
     * @param containerDto containerDto
     * @return Submission
     * @throws NotFoundException NotFoundException
     */
    @Transactional(rollbackFor = {Exception.class, NotFoundException.class},
            propagation = Propagation.REQUIRED)
    @Override
    public Submission assignContainer(@NotNull Submission submission,
                                      @NotNull ContainerDto containerDto)
            throws NotFoundException {

        if (!containerJpaService.exists(containerDto)) {
            throw new NotFoundException("Container does not exist");
        }

        Container container = containerJpaService.findById(containerDto.getId());
        container.setSubmission(submission);
        submission.setContainer(container);

        return save(submission);
    }

    /**
     * Updated submission.
     *
     * @param oldDto oldDto
     * @param newDto newDto
     * @return updated submission
     */
    @Transactional(rollbackFor = {Exception.class, NotFoundException.class},
            propagation = Propagation.REQUIRED)
    @Override
    public Submission update(SubmissionDto oldDto, SubmissionDto newDto) {
        throw new UnsupportedOperationException("Submission cannot be updated");
    }
}
