package nl.tudelft.sp.modelchecker.controller;


import java.util.List;
import javassist.NotFoundException;
import javax.validation.constraints.NotNull;
import nl.tudelft.sp.modelchecker.dto.UserDto;
import nl.tudelft.sp.modelchecker.entities.BasicUser;
import nl.tudelft.sp.modelchecker.services.AdminService;
import nl.tudelft.sp.modelchecker.services.servicebeans.BasicUserJpaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/")
@RestController
public class BasicUserController {

    @Autowired
    BasicUserJpaService basicUserJpaService;

    @Autowired
    AdminService adminService;

    @Autowired
    ModelMapper modelMapper;


    /**
     * Get a user from the database.
     *
     * @param userDto userDto
     * @return specific user
     */
    @PostMapping("get/user")
    @ResponseBody
    public ResponseEntity<UserDto> getUser(@NotNull @RequestBody UserDto userDto) {
        BasicUser user = basicUserJpaService.findById(userDto.getId());

        if (user == null) {
            throw new AuthenticationCredentialsNotFoundException("User not found");
        }

        UserDto responseDto = modelMapper.map(user, UserDto.class);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }


    /**
     * Get a user from the database.
     *
     * @param userDto userDto
     * @return specific user
     */
    @PostMapping("delete/user")
    @PreAuthorize("hasAuthority('APPROLE_ADMIN')")
    @ResponseBody
    public ResponseEntity<UserDto> deleteUser(@NotNull @RequestBody UserDto userDto)
            throws NotFoundException {

        adminService.delete(userDto);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    /**
     * Get a user from the database.
     *
     * @param userDto userDto
     * @return specific user
     */
    @PostMapping("update/user")
    @PreAuthorize("hasAuthority('APPROLE_ADMIN')")
    @ResponseBody
    public ResponseEntity<UserDto> updateUser(@NotNull @RequestBody UserDto userDto)
            throws NotFoundException {

        BasicUser basicUser = basicUserJpaService.update(userDto, userDto);

        UserDto returnDto = modelMapper.map(basicUser, UserDto.class);
        return new ResponseEntity<>(returnDto, HttpStatus.OK);
    }

    /**
     * Get a list of users from database.
     *
     * @return list of users.
     */
    @PostMapping("list/user")
    @PreAuthorize("hasAuthority('APPROLE_ADMIN')")
    @ResponseBody
    public ResponseEntity<List<UserDto>> listUser() {

        List<UserDto> userDtos = basicUserJpaService.convertToUserDtos();

        return new ResponseEntity<>(userDtos, HttpStatus.OK);
    }
}
