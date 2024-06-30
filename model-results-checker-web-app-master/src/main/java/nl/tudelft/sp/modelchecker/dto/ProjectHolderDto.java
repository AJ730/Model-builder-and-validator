package nl.tudelft.sp.modelchecker.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.tudelft.sp.modelchecker.entities.ProjectHolder;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProjectHolderDto extends Dto<Long> {

    private String userId;

    /**
     * Constructor for projectHolder.
     *
     * @param id id
     */
    public ProjectHolderDto(Long id) {
        super(id);
    }

    /**
     * Constructor for projectHolderDto.
     *
     * @param userId userId
     */
    public ProjectHolderDto(String userId) {
        this.userId = userId;
    }

    /**
     * Constructor for projectHolderDto.
     *
     * @param projectHolder projectHolder
     */
    public ProjectHolderDto(ProjectHolder projectHolder) {
        super(projectHolder.getId());

        if (projectHolder.getClient() != null) {
            this.userId = projectHolder.getClient().getId();
        }
    }
}
