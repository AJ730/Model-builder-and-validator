package nl.tudelft.sp.modelchecker.dto;

import java.sql.Date;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.tudelft.sp.modelchecker.entities.User;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserDto extends Dto<String> {

    private String email;
    private String username;
    private Date registrationDate;

    /**
     * Constructor for UserDto.
     *
     * @param email            email
     * @param username         username
     * @param registrationDate registrationTime
     */
    public UserDto(String email, String username, Date registrationDate) {
        this.email = email;
        this.username = username;
        this.registrationDate = registrationDate;
    }


    /**
     * Constructor for user.
     *
     * @param userId userId
     */
    public UserDto(String userId) {
        super(userId);
    }


    /**
     * Convert userDto to user.
     *
     * @param user user
     */
    public UserDto(User user) {
        super(user);

        if (user.getEmail() != null) {
            this.email = user.getEmail();
        }
        if (user.getUsername() != null) {
            this.username = user.getUsername();
        }
        if (user.getRegistrationDate() != null) {
            this.registrationDate = user.getRegistrationDate();
        }
    }

}

