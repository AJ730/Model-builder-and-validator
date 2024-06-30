package nl.tudelft.sp.modelchecker.services;

import javassist.NotFoundException;
import nl.tudelft.sp.modelchecker.dto.UserDto;
import nl.tudelft.sp.modelchecker.entities.User;
import nl.tudelft.sp.modelchecker.exceptions.AuthorityException;
import nl.tudelft.sp.modelchecker.exceptions.DateException;
import nl.tudelft.sp.modelchecker.exceptions.ExistsException;

public interface AuthService {

    /**
     * Register a user.
     *
     * @param oid      oid
     * @param username username
     * @param email    email
     * @param isAdmin  isAdmin
     * @return registered user.
     * @throws ExistsException    ExistsException
     * @throws DateException      DateException
     * @throws NotFoundException  NotFoundException
     * @throws AuthorityException AuthorityException
     */
    User register(String oid, String username, String email, Boolean isAdmin)
            throws ExistsException, DateException, NotFoundException, AuthorityException;

    /**
     * Find by email.
     *
     * @param email email
     * @return user
     * @throws NotFoundException NotFoundException
     */
    UserDto findByEmail(String email) throws NotFoundException;

    /**
     * Validate a user.
     *
     * @param token  token
     * @param oid    oid
     * @param userId userId
     * @throws AuthorityException AuthorityException
     * @throws NotFoundException  NotFoundException
     */
    void validate(String token, String oid, String userId)
            throws AuthorityException, NotFoundException;
}
