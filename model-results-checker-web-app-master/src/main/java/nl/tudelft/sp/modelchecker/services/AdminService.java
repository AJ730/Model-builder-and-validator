package nl.tudelft.sp.modelchecker.services;

import nl.tudelft.sp.modelchecker.dto.UserDto;
import nl.tudelft.sp.modelchecker.entities.Admin;

public interface AdminService extends UserService<Admin> {

    /**
     * Method to check if user is an admin.
     *
     * @param userDto userDto
     * @return boolean
     */
    boolean isAdmin(UserDto userDto);

    /**
     * Method to check if user is an admin.
     *
     * @param userId userId
     * @return boolean
     */
    boolean isAdmin(String userId);
}
