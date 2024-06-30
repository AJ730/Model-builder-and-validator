package nl.tudelft.sp.modelchecker.services.servicebeans;

import java.sql.Date;
import java.time.LocalDate;
import javassist.NotFoundException;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotNull;
import nl.tudelft.sp.modelchecker.dto.UserDto;
import nl.tudelft.sp.modelchecker.entities.User;
import nl.tudelft.sp.modelchecker.exceptions.AuthorityException;
import nl.tudelft.sp.modelchecker.exceptions.DateException;
import nl.tudelft.sp.modelchecker.exceptions.ExistsException;
import nl.tudelft.sp.modelchecker.repositories.UserRepository;
import nl.tudelft.sp.modelchecker.services.UserService;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Transactional(readOnly = true)
@Service
public abstract class UserJpaService<U extends User> extends CrudJpaService<U, String, UserDto>
        implements UserService<U> {


    private final UserRepository<U> userRepository;

    /**
     * Describes the constructor for BasicUserService implementation.
     *
     * @param repository userPersistence
     */
    public UserJpaService(UserRepository<U> repository) {
        super(repository);
        this.userRepository = repository;
    }

    /**
     * Registers a new User.
     *
     * @param newUser an user object with unencrypted username,email and password
     * @return User user that is created
     * @throws ExistsException if user already exists
     */
    @Transactional(rollbackFor = {ExistsException.class, ConstraintViolationException.class},
            propagation = Propagation.REQUIRED)
    @Override
    public U register(@NotNull U newUser) throws ExistsException {
        throw new UnsupportedOperationException("Use registerUser method instead");
    }

    /**
     * Registers a new User.
     *
     * @param newUser an user object with unencrypted username,email and password
     * @return User user that is created
     * @throws DateException if user is registered in the past
     */
    @Transactional(rollbackFor = {DateException.class},
            propagation = Propagation.REQUIRED)
    public U registerUser(@NotNull U newUser) throws DateException, ExistsException {

        Date today = Date.valueOf(LocalDate.now());

        if (exists(newUser)) throw new ExistsException("User already exists");

        if (newUser.getRegistrationDate().before(today)) {
            throw new DateException("Registration cannot take place in the past");
        }

        return save(newUser);
    }

    /**
     * Delete a user.
     *
     * @param userDto user to delete
     * @throws NotFoundException throws when there is no user with that username/email
     */
    @Transactional(rollbackFor = {AuthorityException.class, NotFoundException.class},
            propagation = Propagation.REQUIRED)
    @Override
    public void deleteUser(@NotNull UserDto userDto) throws NotFoundException, AuthorityException {
        super.delete(userDto);
    }


    /**
     * Changes attributes of the user.
     *
     * @param oldDto dto with user id
     * @param newDto dto with field to change
     * @return entity of new User
     * @throws NotFoundException user wasn't found
     */
    @Transactional(rollbackFor = {DataAccessException.class, NotFoundException.class},
            propagation = Propagation.REQUIRED)
    @Override
    public U update(@NotNull UserDto oldDto, @NotNull UserDto newDto) throws NotFoundException {

        if (!exists(oldDto)) throw new NotFoundException("No Such User");

        U current = findById(oldDto.getId());

        current.setUsername(newDto.getUsername());

        return save(current);
    }


    /**
     * Find a user by username.
     *
     * @param username username
     * @return user
     */
    @Override
    public U findByUsername(@NotNull String username) {
        return userRepository.findByUsername(username);
    }


    /**
     * Find a user by email.
     *
     * @param email email
     * @return user
     */
    @Override
    public U findByEmail(@NotNull String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Check if a user exists.
     *
     * @param dto of the entity with the id looked for
     * @return user
     */
    @Override
    public boolean exists(@NotNull UserDto dto) {
        if (findByEmail(dto.getEmail()) != null) return true;
        return super.exists(dto);
    }

    /**
     * check if a user exists.
     *
     * @param type to look for
     * @return user
     */
    @Override
    public boolean exists(@NotNull U type) {
        if (findByEmail(type.getEmail()) != null) return true;
        return super.exists(type);
    }

}

