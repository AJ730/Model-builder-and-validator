package nl.tudelft.sp.modelchecker.services.servicebeans;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import nl.tudelft.sp.modelchecker.dto.UserDto;
import nl.tudelft.sp.modelchecker.entities.Admin;
import nl.tudelft.sp.modelchecker.entities.User;
import nl.tudelft.sp.modelchecker.repositories.UserRepository;
import nl.tudelft.sp.modelchecker.services.AdminService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AdminJpaService extends UserJpaService<Admin> implements AdminService {

    private final UserRepository<Admin> adminRepository;

    /**
     * Describes the constructor for BasicUserService implementation.
     *
     * @param userRepository userPersistence
     */
    public AdminJpaService(UserRepository<Admin> userRepository) {
        super(userRepository);
        this.adminRepository = userRepository;
    }

    /**
     * Is the user an admin.
     *
     * @param userDto userDto
     * @return boolean
     */
    @Override
    public boolean isAdmin(UserDto userDto) {
        return isAdmin(userDto.getId());
    }

    /**
     * Is the user an admin.
     *
     * @param userId userId
     * @return boolean
     */
    @Override
    public boolean isAdmin(String userId) {
        return findById(userId) instanceof Admin;
    }


    /**
     * Return the number of admins.
     *
     * @return count
     */
    @Override
    public int count() {
        int c = 0;
        for (User user : adminRepository.findAll()) {
            if (user instanceof Admin) c++;
        }
        return c;
    }

    /**
     * Convert to userDtos.
     *
     * @return list of userDtos
     */
    @Override
    public List<UserDto> convertToUserDtos() {
        List<Admin> basicUsers = new ArrayList<>();
        for (User user : adminRepository.findAll()) {
            if (user instanceof Admin) {
                basicUsers.add((Admin) user);
            }
        }
        return basicUsers.stream().map(UserDto::new)
                .collect(Collectors.toList());
    }
}
