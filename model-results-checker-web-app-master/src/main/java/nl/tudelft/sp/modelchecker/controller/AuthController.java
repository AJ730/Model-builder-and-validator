package nl.tudelft.sp.modelchecker.controller;

import javassist.NotFoundException;
import nl.tudelft.sp.modelchecker.dto.UserDto;
import nl.tudelft.sp.modelchecker.entities.User;
import nl.tudelft.sp.modelchecker.exceptions.AuthorityException;
import nl.tudelft.sp.modelchecker.exceptions.DateException;
import nl.tudelft.sp.modelchecker.exceptions.ExistsException;
import nl.tudelft.sp.modelchecker.services.servicebeans.AuthJpaService;
import nl.tudelft.sp.modelchecker.services.servicebeans.JwtTokenJpaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/api/")
@RestController
public class AuthController {

    @Autowired
    JwtTokenJpaService jwtTokenJpaService;

    @Autowired
    AuthJpaService authJpaService;

    @Autowired
    ModelMapper modelMapper;

    /**
     * Get user information.
     *
     * @param token token.
     * @return user information
     * @throws DateException      DateException
     * @throws ExistsException    ExistsException
     * @throws NotFoundException  NotFoundException
     * @throws AuthorityException AuthorityException
     */
    @GetMapping("get/UserInfo")
    @ResponseBody
    public ResponseEntity<UserDto> registerUser(@RequestHeader("Authorization") String token)
            throws DateException, ExistsException, NotFoundException, AuthorityException {

        User user = jwtTokenJpaService.parseToken(token);
        UserDto userDto = modelMapper.map(user, UserDto.class);

        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    /**
     * Get oid of a user from email.
     *
     * @param email email
     * @return user
     * @throws NotFoundException NotFoundException
     */
    @GetMapping("get/oid")
    @ResponseBody
    public ResponseEntity<UserDto> getOid(@RequestParam String email)
            throws NotFoundException {

        UserDto returnDto = authJpaService.findByEmail(email);

        return new ResponseEntity<>(returnDto, HttpStatus.OK);
    }
}
