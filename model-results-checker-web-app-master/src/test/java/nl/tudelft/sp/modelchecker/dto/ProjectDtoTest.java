package nl.tudelft.sp.modelchecker.dto;

import static org.assertj.core.api.Assertions.assertThat;

import nl.tudelft.sp.modelchecker.entities.Admin;
import nl.tudelft.sp.modelchecker.entities.Project;
import nl.tudelft.sp.modelchecker.entities.ProjectHolder;
import org.junit.jupiter.api.Test;

public class ProjectDtoTest {

    /**
     * constructor test.
     */
    @Test
    void constructorTest() {

        // arrange
        Project project = new Project();
        project.setId(1L);
        project.setProjectHolder(new ProjectHolder());
        project.setAdmin(new Admin());

        // act
        ProjectDto projectDto = new ProjectDto(project);


        // assert
        assertThat(projectDto.getProjectHolderId()).isNull();
        assertThat(projectDto.getTitle()).isNull();
        assertThat(projectDto.getDescription()).isNull();
        assertThat(projectDto.getAdminId()).isNull();

    }

}
