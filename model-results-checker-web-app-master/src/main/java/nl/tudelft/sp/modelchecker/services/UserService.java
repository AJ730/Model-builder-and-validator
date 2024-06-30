package nl.tudelft.sp.modelchecker.services;

import java.util.List;
import javassist.NotFoundException;
import nl.tudelft.sp.modelchecker.dto.UserDto;
import nl.tudelft.sp.modelchecker.entities.User;
import nl.tudelft.sp.modelchecker.exceptions.AuthorityException;
import nl.tudelft.sp.modelchecker.exceptions.DateException;
import nl.tudelft.sp.modelchecker.exceptions.ExistsException;

public interface UserService<U extends User> extends CrudService<U, String, UserDto> {

    /**
     * Find by username.
     *
     * @param username username
     * @return type U
     */
    U findByUsername(String username);

    /**
     * Find by email.
     *
     * @param email email
     * @return type u
     */
    U findByEmail(String email);

    /**
     * Delete a user.
     *
     * @param userDto userDto
     * @throws NotFoundException NotFoundException
     */
    void deleteUser(UserDto userDto) throws NotFoundException, AuthorityException;

    /**
     * Register a user.
     *
     * @param newUser newUser
     * @return registered User
     * @throws DateException DateException
     */
    U registerUser(U newUser) throws DateException, ExistsException;

    /**
     * Convert users to UserDtos.
     *
     * @return list of UserDto
     */
    List<UserDto> convertToUserDtos();

}
