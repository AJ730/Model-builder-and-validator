package nl.tudelft.sp.modelchecker.services.servicebeans;

import java.sql.Date;
import java.time.LocalDate;
import javassist.NotFoundException;
import nl.tudelft.sp.modelchecker.dto.UserDto;
import nl.tudelft.sp.modelchecker.entities.Admin;
import nl.tudelft.sp.modelchecker.entities.BasicUser;
import nl.tudelft.sp.modelchecker.entities.ProjectHolder;
import nl.tudelft.sp.modelchecker.entities.User;
import nl.tudelft.sp.modelchecker.exceptions.AuthorityException;
import nl.tudelft.sp.modelchecker.exceptions.DateException;
import nl.tudelft.sp.modelchecker.exceptions.ExistsException;
import nl.tudelft.sp.modelchecker.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthJpaService implements AuthService {

    @Autowired
    BasicUserJpaService basicUserJpaService;

    @Autowired
    AdminJpaService adminJpaService;

    @Autowired
    JwtTokenJpaService jwtTokenJpaService;

    @Autowired
    ProjectHolderJpaService projectHolderJpaService;

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
    @Transactional(rollbackFor = {Exception.class},
            propagation = Propagation.REQUIRED)
    @Override
    public User register(String oid, String username, String email, Boolean isAdmin)
            throws ExistsException, DateException, NotFoundException, AuthorityException {

        if (isAdmin) {
            Admin admin = adminJpaService.findById(oid);
            return admin == null ? adminJpaService.registerUser(
                    new Admin(oid, username, email, Date.valueOf(LocalDate.now()))) : admin;
        }

        BasicUser basicUser = basicUserJpaService.findById(oid);
        return basicUser == null ? registerSynchronousUser(oid, username, email)
                : basicUser;
    }

    /**
     * Find a user by email.
     *
     * @param email email
     * @return user
     * @throws NotFoundException NotFoundException
     */
    @Override
    public UserDto findByEmail(String email) throws NotFoundException {
        User user = adminJpaService.findByEmail(email);

        if (user == null) throw new NotFoundException("User not found");
        return new UserDto(user);
    }

    /**
     * Validate a user.
     *
     * @param token  token
     * @param oid    oid
     * @param userId userId
     * @throws AuthorityException AuthorityException
     * @throws NotFoundException  NotFoundException
     */
    @Override
    public void validate(String token, String oid, String userId)
            throws AuthorityException, NotFoundException {
        if (!verify(token, oid, userId))
            throw new AuthorityException("User can only request their own information");
    }


    /**
     * Verify a user.
     *
     * @param token  token
     * @param oid    oid
     * @param userId userId
     * @return verified user
     * @throws NotFoundException NotFoundException
     */
    private boolean verify(String token, String oid, String userId) throws NotFoundException {
        if (userId == null) throw new NotFoundException("User not found");
        jwtTokenJpaService.decodeToken(token);

        return oid.equals(userId) | jwtTokenJpaService.isAdmin();
    }


    /**
     * Register a user with projectHolder.
     *
     * @param oid      oid
     * @param username username
     * @param email    email
     * @return registered user
     * @throws ExistsException    ExistsException
     * @throws NotFoundException  NotFoundException
     * @throws AuthorityException AuthorityException
     * @throws DateException      DateException
     */
    private BasicUser registerSynchronousUser(String oid, String username, String email) throws
            ExistsException, NotFoundException, AuthorityException, DateException {

        BasicUser currentUser = basicUserJpaService.registerUser(
                new BasicUser(oid, username, email, Date.valueOf(LocalDate.now())));

        projectHolderJpaService.register(new ProjectHolder(), new UserDto(currentUser));
        return currentUser;
    }
}
