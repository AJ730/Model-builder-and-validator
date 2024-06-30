package nl.tudelft.sp.modelchecker.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.tudelft.sp.modelchecker.entities.Project;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProjectDto extends Dto<Long> {

    private String title;

    private String description;

    private Long projectHolderId;

    private String adminId;


    /**
     * Constructor for ProjectDto.
     *
     * @param id id
     */
    public ProjectDto(long id) {
        super(id);
    }

    /**
     * Constructor for ProjectDto.
     *
     * @param title           title
     * @param description     description
     * @param projectHolderId projectHolderId
     * @param adminId         adminId
     */
    public ProjectDto(String title, String description, Long projectHolderId, String adminId) {
        this.title = title;
        this.description = description;
        this.projectHolderId = projectHolderId;
        this.adminId = adminId;
    }

    /**
     * Constructor for ProjectDto.
     *
     * @param title       title
     * @param description description
     */
    public ProjectDto(String title, String description) {
        this.title = title;
        this.description = description;
    }

    /**
     * Constructor for ProjectDto.
     *
     * @param project project
     */
    public ProjectDto(Project project) {
        super(project.getId());

        if (project.getTitle() != null) {
            this.title = project.getTitle();
        }
        if (project.getDescription() != null) {
            this.description = project.getDescription();
        }
        if (project.getProjectHolder().getId() != null) {
            this.projectHolderId = project.getProjectHolder().getId();
        }
        if (project.getAdmin().getId() != null) {
            this.adminId = project.getAdmin().getId();
        }
    }

}
