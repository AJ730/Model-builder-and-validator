package nl.tudelft.sp.modelchecker.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SubmissionDto extends Dto<Long> {

    private Long containerId;

    private String clientID;

    private Long csvId;


    /**
     * Constructor for SubmissionDto.
     *
     * @param submissionId submissionId
     */
    public SubmissionDto(Long submissionId) {
        super(submissionId);
    }

}
