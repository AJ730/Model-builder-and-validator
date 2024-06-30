package nl.tudelft.sp.modelchecker.services.servicebeans;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import nl.tudelft.sp.modelchecker.dto.UserDto;
import nl.tudelft.sp.modelchecker.entities.BasicUser;
import nl.tudelft.sp.modelchecker.entities.User;
import nl.tudelft.sp.modelchecker.exceptions.AuthorityException;
import nl.tudelft.sp.modelchecker.repositories.UserRepository;
import nl.tudelft.sp.modelchecker.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BasicUserJpaService extends UserJpaService<BasicUser>
        implements UserService<BasicUser> {


    private final UserRepository<BasicUser> repository;

    @Autowired
    ModelMapper modelMapper;

    /**
     * Describes the constructor for BasicUserService implementation.
     *
     * @param repository userPersistence
     */
    public BasicUserJpaService(UserRepository<BasicUser> repository) {
        super(repository);
        this.repository = repository;
    }

    /**
     * Users are not allowed to deleted other users.
     *
     * @param userDto user to delete
     */
    @Override
    public void deleteUser(UserDto userDto) throws AuthorityException {
        throw new AuthorityException("Only admins are allowed to delete users");
    }

    /**
     * Users are not allowed to delete.
     *
     * @param userDto userDto
     */
    @Override
    public void delete(UserDto userDto) {
        throw new UnsupportedOperationException("Only admins are allowed to delete users");
    }


    /**
     * Count the number of basicUsers.
     *
     * @return count
     */
    @Override
    public int count() {
        int c = 0;
        for (User user : repository.findAll()) {
            if (user instanceof BasicUser) {
                c++;
            }
        }
        return c;
    }


    /**
     * Convert list of users to Dtos.
     *
     * @return converted list
     */
    @Override
    public List<UserDto> convertToUserDtos() {
        List<BasicUser> basicUsers = new ArrayList<>();
        for (User user : repository.findAll()) {
            if (user instanceof BasicUser) {
                basicUsers.add((BasicUser) user);
            }
        }
        return basicUsers.stream().map(UserDto::new)
                .collect(Collectors.toList());
    }
}
