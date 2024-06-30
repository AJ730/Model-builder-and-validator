package nl.tudelft.sp.modelchecker.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ContainerDtoTest {

    private ContainerDto containerDto1;

    private ContainerDto containerDto2;

    @BeforeEach
    void setUp() {

        // arrange
        containerDto1 = new ContainerDto(1L, "output.mp4", 23.0);
        containerDto2 = new ContainerDto(3L, 24.0, "test", "video.mp4", "desc");
    }

    /**
     * constructor tests.
     */
    @Test
    void constructorTest() {
        assertThat(containerDto1.getCsvId()).isEqualTo(1L);
        assertThat(containerDto1.getBlobName()).isEqualTo("output.mp4");
        assertThat(containerDto1.getFrameRate()).isEqualTo(23.0);
        assertThat(containerDto2.getProjectId()).isEqualTo(3L);
        assertThat(containerDto2.getFrameRate()).isEqualTo(24.0);
        assertThat(containerDto2.getName()).isEqualTo("test");
        assertThat(containerDto2.getBlobName()).isEqualTo("video.mp4");
        assertThat(containerDto2.getDescription()).isEqualTo("desc");
    }
}
