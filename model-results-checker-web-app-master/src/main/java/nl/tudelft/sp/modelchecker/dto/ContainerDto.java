package nl.tudelft.sp.modelchecker.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.tudelft.sp.modelchecker.entities.Container;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ContainerDto extends Dto<Long> {

    private String blobName;

    private Long csvId;

    private Long projectId;

    private Long persistentCsvId;

    private Long submissionId;

    private Double frameRate;

    private String name;

    private String description;

    private String csvName;

    private String className;

    /**
     * Constructor for containerDto.
     *
     * @param containerId containerId
     */
    public ContainerDto(long containerId) {
        super(containerId);
    }


    /**
     * constructor.
     *
     * @param csvId csvId
     * @param blobName blobName
     * @param frameRate frameRate
     */
    public ContainerDto(Long csvId, String blobName, double frameRate) {
        this.blobName = blobName;
        this.csvId = csvId;
        this.frameRate = frameRate;
    }

    /**
     * Constructor for containerDto.
     *
     * @param projectId   projectId
     * @param frameRate   frameRate
     * @param name        name
     * @param description description
     */
    public ContainerDto(Long projectId, double frameRate,
                        String name, String blobName, String description) {
        this.projectId = projectId;
        this.frameRate = frameRate;
        this.name = name;
        this.description = description;
        this.blobName  = blobName;
    }

    /**
     * Constructor for containerDto.
     *
     * @param container container
     */
    public ContainerDto(Container container) {
        super(container.getId());
        if (container.getCsv() != null) {
            csvId = container.getCsv().getId();
        }
        if (container.getProject() != null) {
            projectId = container.getProject().getId();
        }
        if (container.getSubmission() != null) {
            submissionId = container.getSubmission().getId();
        }
        if (container.getPersistentCSv() != null) {
            persistentCsvId = container.getPersistentCSv().getId();
        }

        frameRate = container.getFrameRate();
        name = container.getName();
        blobName = container.getBlobName();
        description = container.getDescription();
        csvName = container.getCsvName();
        className = container.getClassName();
    }

}
