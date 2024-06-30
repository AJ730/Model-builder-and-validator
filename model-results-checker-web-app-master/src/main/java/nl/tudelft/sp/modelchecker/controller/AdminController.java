package nl.tudelft.sp.modelchecker.controller;

import java.util.List;
import javassist.NotFoundException;
import javax.validation.constraints.NotNull;
import nl.tudelft.sp.modelchecker.dto.UserDto;
import nl.tudelft.sp.modelchecker.entities.Admin;
import nl.tudelft.sp.modelchecker.exceptions.AuthorityException;
import nl.tudelft.sp.modelchecker.services.AdminService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
@PreAuthorize("hasAuthority('APPROLE_ADMIN')")
public class AdminController {

    @Autowired
    AdminService adminService;

    @Autowired
    ModelMapper modelMapper;

    /**
     * Get a specific user.
     *
     * @param userDto userDto
     * @return UserDto
     */
    @PostMapping("get/admin")
    @ResponseBody
    public ResponseEntity<UserDto> getAdmin(@RequestBody UserDto userDto) {
        Admin admin = adminService.findById(userDto.getId());

        if (admin == null) {
            throw new AuthenticationCredentialsNotFoundException("Admin not found");
        }
        UserDto returnDto = modelMapper.map(admin, UserDto.class);
        return new ResponseEntity<>(returnDto, HttpStatus.OK);
    }

    /**
     * Update an admin.
     *
     * @param userDto userDto
     * @return updated admin
     * @throws NotFoundException NotFoundException
     */
    @PostMapping("update/admin")
    @ResponseBody
    public ResponseEntity<UserDto> updateUser(@NotNull @RequestBody UserDto userDto)
            throws NotFoundException {

        Admin admin = adminService.update(userDto, userDto);

        UserDto returnDto = modelMapper.map(admin, UserDto.class);
        return new ResponseEntity<>(returnDto, HttpStatus.OK);
    }

    /**
     * Delete an admin.
     *
     * @param userDto userDto
     * @return deleted admin
     */
    @PostMapping("delete/admin")
    @ResponseBody
    public ResponseEntity<UserDto> deleteUser(@NotNull @RequestBody UserDto userDto)
            throws NotFoundException, AuthorityException {

        adminService.deleteUser(userDto);

        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    /**
     * Get a list of admins from database.
     *
     * @return list of admmins
     */
    @PostMapping("list/admin")
    @ResponseBody
    public ResponseEntity<List<UserDto>> listAdmin() {

        List<UserDto> userDtos = adminService.convertToUserDtos();
        return new ResponseEntity<>(userDtos, HttpStatus.OK);
    }

}
