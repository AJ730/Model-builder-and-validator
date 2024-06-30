package nl.tudelft.sp.modelchecker.controller;

import java.io.IOException;
import javassist.NotFoundException;
import nl.tudelft.sp.modelchecker.dto.RecordListDto;
import nl.tudelft.sp.modelchecker.dto.SubmissionDto;
import nl.tudelft.sp.modelchecker.entities.Submission;
import nl.tudelft.sp.modelchecker.exceptions.AuthorityException;
import nl.tudelft.sp.modelchecker.exceptions.ExistsException;
import nl.tudelft.sp.modelchecker.services.SubmissionService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
public class SubmissionController
        extends AbstractController<Long, SubmissionService, Submission, SubmissionDto> {


    /**
     * Create a submission controller.
     *
     * @param service     service
     * @param modelMapper modelMapper
     */
    public SubmissionController(SubmissionService service, ModelMapper modelMapper) {
        super(service, modelMapper);
    }

    /**
     * create submission.
     *
     * @param clientId      clientId
     * @param recordListDto recordListDto
     * @return response entity
     * @throws NotFoundException  NotFoundException
     * @throws ExistsException    ExistsException
     * @throws IOException        IOException
     * @throws AuthorityException AuthorityException
     */
    @PostMapping("/create/submission")
    public ResponseEntity<Void> create(@RequestHeader("oid") String clientId,
                                       @RequestBody RecordListDto recordListDto)
            throws NotFoundException, ExistsException, IOException, AuthorityException {

        service.register(recordListDto, clientId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * endpoint for deleting submission.
     *
     * @param submissionDto submissionDto
     * @return ResponseEntity of submission dto
     * @throws NotFoundException NotFoundException if submission does not exist
     */
    @PostMapping("/delete/submission")
    @ResponseBody
    public ResponseEntity<SubmissionDto> delete(@RequestBody SubmissionDto submissionDto)
            throws NotFoundException {
        return super.delete(submissionDto);
    }
}
