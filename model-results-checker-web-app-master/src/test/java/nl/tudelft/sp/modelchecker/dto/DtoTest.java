package nl.tudelft.sp.modelchecker.dto;

import static org.assertj.core.api.Assertions.assertThat;

import nl.tudelft.sp.modelchecker.entities.SuperEntity;
import org.junit.jupiter.api.Test;

public class DtoTest {

    /**
     * constructor test.
     */
    @Test
    void constructorTest() {
        // arrange
        SuperEntity superEntity = null;

        // act
        Dto dto = new Dto(superEntity);

        // assert
        assertThat(dto.getId()).isNull();

    }
}
