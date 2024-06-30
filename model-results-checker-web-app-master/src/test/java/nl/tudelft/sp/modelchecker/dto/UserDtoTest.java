package nl.tudelft.sp.modelchecker.dto;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.sql.Date;
import java.time.LocalDate;
import nl.tudelft.sp.modelchecker.entities.BasicUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserDtoTest {

    private BasicUser user;

    private UserDto userDto;

    private UserDto userDto1;

    private String username = "username";
    private String username1 = "username1";

    private String email = "a@gmail.com";
    private String email1 = "b@gmail.com";

    private Date date = Date.valueOf(LocalDate.now());

    @BeforeEach
    void setUp() {

        // arrange
        user = new BasicUser();

        // act
        userDto1 = new UserDto(email1, username1,
                date);
        userDto = new UserDto(user);
    }

    /**
     * constructor test.
     */
    @Test
    void constructorTest() {

        // assert
        assertThat(userDto1.getUsername()).isEqualTo(username1);
        assertThat(userDto1.getEmail()).isEqualTo(email1);

        assertThat(userDto.getEmail()).isNull();
        assertThat(userDto.getUsername()).isNull();

        // arrange
        user.setUsername(username);

        // act
        userDto = new UserDto(user);

        // assert
        assertThat(userDto.getUsername()).isEqualTo(username);

        // arrange
        user.setEmail(email);

        // act
        userDto = new UserDto(user);

        // assert
        assertThat(userDto.getEmail()).isEqualTo(email);

        // arrange
        user.setRegistrationDate(date);

        // act
        userDto = new UserDto(user);

        // assert
        assertThat(userDto.getRegistrationDate()).isEqualTo(date);

    }
}
