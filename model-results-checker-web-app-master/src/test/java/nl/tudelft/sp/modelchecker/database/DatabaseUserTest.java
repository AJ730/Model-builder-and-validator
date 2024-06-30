package nl.tudelft.sp.modelchecker.database;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import javassist.NotFoundException;
import nl.tudelft.sp.modelchecker.Application;
import nl.tudelft.sp.modelchecker.dto.UserDto;
import nl.tudelft.sp.modelchecker.entities.Admin;
import nl.tudelft.sp.modelchecker.entities.BasicUser;
import nl.tudelft.sp.modelchecker.exceptions.AuthorityException;
import nl.tudelft.sp.modelchecker.exceptions.DateException;
import nl.tudelft.sp.modelchecker.exceptions.ExistsException;
import nl.tudelft.sp.modelchecker.services.servicebeans.AdminJpaService;
import nl.tudelft.sp.modelchecker.services.servicebeans.BasicUserJpaService;
import nl.tudelft.sp.modelchecker.services.servicebeans.ProjectJpaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Transactional
public class DatabaseUserTest {

    @Autowired
    private BasicUserJpaService basicUserJpaService;

    @Autowired
    private AdminJpaService adminJpaService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ProjectJpaService projectJpaService;


    private BasicUser akash;
    private UserDto akashDto;
    private UserDto adminDto;
    private BasicUser ege;
    private String akashId;

    /**
     * Set Up the test environment.
     *
     * @throws ExistsException ExistsException
     */
    @BeforeEach
    public void setUp() throws ExistsException, DateException {

        // arrange
        akash = new BasicUser("1A", "akash", "amalan_akash@hotmail.com",
                Date.valueOf(LocalDate.now()));
        ege = new BasicUser("2A", "ege", "ege_dumnali@hotmail.com",
                Date.valueOf(LocalDate.now()));
        Admin admin = new Admin("3A", "admin", "logan_paul@gmail.com",
                Date.valueOf(LocalDate.now()));

        admin = adminJpaService.registerUser(admin);
        adminDto = modelMapper.map(admin, UserDto.class);
        akash = basicUserJpaService.registerUser(akash);
        akashDto = modelMapper.map(akash, UserDto.class);
        akashId = akash.getId();
    }

    /**
     * Register a new user test.
     */
    @Test
    public void registerNewUserTest() {

        // assert
        assertThat(basicUserJpaService.findByEmail("amalan_akash@hotmail.com")).isNotNull();
        assertThat(basicUserJpaService.findByUsername("akash")).isNotNull();
    }

    /**
     * Register multiple users.
     *
     * @throws DateException   DateException
     * @throws ExistsException ExistsException
     */
    @Test
    public void registerMultipleUsersTest() throws DateException, ExistsException {

        // act
        ege = basicUserJpaService.registerUser(ege);

        // assert
        assertThat(basicUserJpaService.count()).isEqualTo(2);
        assertThat(basicUserJpaService.findById(ege.getId())).isNotNull();
    }

    /**
     * Register user after today.
     *
     * @throws ExistsException ExistsException
     * @throws DateException   DateException
     */
    @Test
    public void registerUserAfterTest() throws ExistsException, DateException {

        // arrange
        BasicUser user = new BasicUser("2A", "test", "k@gmail.com", Date.valueOf("2100-06-11"));

        // act
        user = basicUserJpaService.registerUser(user);

        // assert
        assertThat(basicUserJpaService.findById(user.getId())).isEqualTo(user);
    }

    /**
     * Register user in the past.
     */
    @Test
    public void registerUserInvalidBeforeTest() {

        // arrange
        BasicUser user = new BasicUser("2A", "test", "k@gmail.com", Date.valueOf("1998-05-11"));

        // act and assert
        assertThrows(DateException.class, () -> {
            basicUserJpaService.registerUser(user);
        });
    }

    /**
     * Count admins in the repo.
     */
    @Test
    public void countAdminsTest() {

        // act and assert
        assertThat(adminJpaService.count()).isEqualTo(1);
    }

    /**
     * Register Already Existing User.
     */
    @Test
    public void registerExistingUserTest() {

        // act and assert
        assertThrows(ExistsException.class, () -> basicUserJpaService.registerUser(akash));
    }

    /**
     * Test whether a type exists.
     */
    @Test
    public void existsTypeTest() {

        // act and assert
        assertTrue(basicUserJpaService.exists(akash));
    }

    /**
     * Test whether a dto exists.
     */
    @Test
    public void existsDtoTest() {

        // act and assert
        assertTrue(basicUserJpaService.exists(akashDto));
    }

    /**
     * Delete the user only with deleteUser() method.
     *
     * @throws UnsupportedOperationException UnsupportedOperationException
     */
    @Test
    public void deleteUserTest() throws UnsupportedOperationException {

        // act and assert
        assertThrows(UnsupportedOperationException.class, () -> {
            basicUserJpaService.delete(akashDto);
        });
    }

    /**
     * Delete non existent user test.
     */
    @Test
    public void deleteNonExistentUserTest() {

        // arrange
        UserDto egeDto = modelMapper.map(ege, UserDto.class);

        // act and assert
        assertThrows(AuthorityException.class, () ->
                basicUserJpaService.deleteUser(egeDto));
    }

    /**
     * Find using the username test.
     */
    @Test
    public void findByUsernameTest() {

        // act and assert
        assertThat(basicUserJpaService.findByUsername("akash")).isNotNull();
    }

    /**
     * Find by email test.
     */
    @Test
    public void findByEmailTest() {

        // act and assert
        assertThat(basicUserJpaService.findByEmail("amalan_akash@hotmail.com")).isNotNull();
    }

    /**
     * Update test.
     *
     * @throws NotFoundException NotFoundException
     */
    @Test
    public void updateTest() throws NotFoundException {

        // arrange
        UserDto updatedDto = modelMapper.map(akash, UserDto.class);
        updatedDto.setUsername("jake");

        // act
        basicUserJpaService.update(akashDto, updatedDto);

        // assert
        assertThat(basicUserJpaService.findById(akashId)
                .getUsername()).isEqualTo("jake");

    }

    /**
     * Update not found test.
     */
    @Test
    public void updateNotFoundTest() {

        // arrange
        UserDto egeDto = modelMapper.map(ege, UserDto.class);
        UserDto updatedDto = modelMapper.map(ege, UserDto.class);
        updatedDto.setUsername("jake");

        // act and assert
        assertThrows(NotFoundException.class, () -> {
            basicUserJpaService.update(egeDto, updatedDto);
        });

    }

    /**
     * Test whether a dto is an admin.
     */
    @Test
    public void adminBadWeatherTest() {

        // act and assert
        assertThat(adminJpaService.isAdmin(akashDto)).isFalse();
    }

    /**
     * Test whether a dto is an admin.
     */
    @Test
    public void adminGoodWeatherTest() {

        // act and assert
        assertThat(adminJpaService.isAdmin(adminDto)).isTrue();
    }

    /**
     * Delete an admin.
     *
     * @throws NotFoundException NotFoundException
     */
    @Test
    public void deleteUserAdminTest() throws NotFoundException, AuthorityException {

        // act
        adminJpaService.deleteUser(akashDto);

        // assert
        assertThat(adminJpaService.findById(akashId)).isNull();

        // act
        adminJpaService.deleteUser(adminDto);

        // assert
        assertThat(adminJpaService.exists(akashDto)).isFalse();
    }

    /**
     * Delete a native user.
     */
    @Test
    public void deleteUserNativeAdminTest() {

        // act and assert
        assertThrows(UnsupportedOperationException.class, () -> {
            basicUserJpaService.delete(akashDto);
        });
    }

    /**
     * Cannot use wrong method.
     */
    @Test
    public void registerUsingWrongMethod() {

        // act and assert
        assertThrows(UnsupportedOperationException.class, () -> {
            basicUserJpaService.register(akash);
        });
    }

    /**
     * Convert basic users to dtos.
     *
     * @throws DateException DateException
     */
    @Test
    public void convertToBasicUserDtoTest() throws DateException, ExistsException {

        // arrange
        basicUserJpaService.registerUser(ege);

        // act
        List<UserDto> userDtoList = basicUserJpaService.convertToUserDtos();

        // assert
        assertThat(userDtoList.size()).isEqualTo(2);
    }

    /**
     * Convert admins to dtos.
     */
    @Test
    public void convertToAdminDtoTest() {

        // act
        List<UserDto> userDtoList = adminJpaService.convertToUserDtos();

        // assert
        assertThat(userDtoList.size()).isEqualTo(1);
    }


    /**
     * FindById test.
     */
    @Test
    public void findByIdGoodWeatherTest() {

        // act and assert
        assertThat(adminJpaService.findById(adminDto.getId())).isNotNull();
    }

    /**
     * FindById test.
     */
    @Test
    public void findByIdTest() {

        // act and assert
        assertThrows(
                AssertionError.class, () ->
                        adminJpaService.findById(null));
    }


    /**
     * Does a dto exist.
     */
    @Test
    public void dtoDoesNotExistTest() {

        // act and assert
        assertThat(adminJpaService.exists(new UserDto())).isFalse();

    }

    /**
     * Is a collection empty.
     *
     * @throws NotFoundException NotFoundException
     */
    @Test
    public void emptyExistsTest() throws NotFoundException {

        // arrange
        adminJpaService.delete(adminDto);

        // act and assert
        assertThat(adminJpaService.exists(adminDto)).isFalse();
    }

    /**
     * Delete the id of a user.
     */
    @Test
    public void deleteByIdTest() {

        // act and assert
        assertThrows(
                NotFoundException.class, () ->
                        adminJpaService.deleteById("sd"));
    }

}
