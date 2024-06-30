package nl.tudelft.sp.modelchecker.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RecordDtoTest {

    private RecordDto recordDto;

    @BeforeEach
    void setUp() {

        // act
        recordDto = new RecordDto(1L);
    }

    /**
     * constructor test.
     */
    @Test
    void constructorTest() {

        // assert
        assertThat(recordDto.getId()).isEqualTo(1L);
    }
}
