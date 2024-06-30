package nl.tudelft.sp.modelchecker.dto;

import static org.assertj.core.api.Assertions.assertThat;

import nl.tudelft.sp.modelchecker.entities.ProjectHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProjectHolderDtoTest {

    private ProjectHolderDto projectHolderDto;

    private ProjectHolderDto projectHolderDto1;

    private ProjectHolder projectHolder;

    @BeforeEach
    void setUp() {

        // arrange
        projectHolder = new ProjectHolder();

        // act
        projectHolderDto = new ProjectHolderDto("id");
        projectHolderDto1 = new ProjectHolderDto(projectHolder);
    }

    /**
     * constructor test.
     */
    @Test
    void constructorTest() {

        // assert
        assertThat(projectHolderDto.getUserId()).isEqualTo("id");
        assertThat(projectHolderDto1.getUserId()).isNull();
    }
}
