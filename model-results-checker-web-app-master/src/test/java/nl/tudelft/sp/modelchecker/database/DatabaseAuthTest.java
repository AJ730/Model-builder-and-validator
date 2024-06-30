package nl.tudelft.sp.modelchecker.database;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Scanner;
import javassist.NotFoundException;
import nl.tudelft.sp.modelchecker.Application;
import nl.tudelft.sp.modelchecker.dto.UserDto;
import nl.tudelft.sp.modelchecker.entities.Admin;
import nl.tudelft.sp.modelchecker.entities.BasicUser;
import nl.tudelft.sp.modelchecker.entities.User;
import nl.tudelft.sp.modelchecker.exceptions.AuthorityException;
import nl.tudelft.sp.modelchecker.exceptions.DateException;
import nl.tudelft.sp.modelchecker.exceptions.ExistsException;
import nl.tudelft.sp.modelchecker.services.servicebeans.AdminJpaService;
import nl.tudelft.sp.modelchecker.services.servicebeans.AuthJpaService;
import nl.tudelft.sp.modelchecker.services.servicebeans.BasicUserJpaService;
import nl.tudelft.sp.modelchecker.services.servicebeans.ProjectHolderJpaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
public class DatabaseAuthTest {

    @Autowired
    AuthJpaService authJpaService;

    @Autowired
    AdminJpaService adminJpaService;

    @Autowired
    BasicUserJpaService basicUserJpaService;

    @Autowired
    ProjectHolderJpaService projectHolderJpaService;


    private String adminId;
    private String userId;

    private Admin admin;
    private BasicUser basicUser;

    private String token1;
    private String token2;

    private String test = "test";


    @BeforeEach
    void setUp() throws ExistsException, DateException, FileNotFoundException {

        // arrange
        admin = new Admin("A1", test, "aka@gmail.com",
                Date.valueOf(LocalDate.now()));
        admin = adminJpaService.registerUser(admin);
        adminId = admin.getId();

        basicUser = new BasicUser("A2", "testg", "gafe@gmail.com",
                Date.valueOf(LocalDate.now()));
        basicUser = basicUserJpaService.registerUser(basicUser);
        userId = basicUser.getId();

        File file = new File("src/test/resources/Tokens.txt");
        FileReader fr = new FileReader(file);
        Scanner sc = new Scanner(fr);
        token1 = sc.nextLine();
        token2 = sc.nextLine();
        sc.close();
    }


    /**
     * Register projectHolder when registering BasicUser.
     */
    @Test
    public void registerAdminTest()
            throws ExistsException, DateException, NotFoundException, AuthorityException {

        // act
        User admin = authJpaService.register("A3", test, "live@gmail.com",
                true);

        // assert
        assertThat(admin).isNotNull();
        assertThat(adminJpaService.count()).isEqualTo(2);
    }

    /**
     * Register already existing user test.
     *
     * @throws ExistsException    ExistsException
     * @throws DateException      DateException
     * @throws NotFoundException  NotFoundException
     * @throws AuthorityException AuthorityException
     */
    @Test
    public void registerAdminAlreadyFoundTest()
            throws ExistsException, DateException, NotFoundException, AuthorityException {

        // act
        User user = authJpaService.register(adminId, test, "aka@gmail.com", true);

        // assert
        assertThat(user).isNotNull();
        assertThat(adminJpaService.count()).isEqualTo(1);
    }

    /**
     * Register a basic user test.
     *
     * @throws ExistsException    ExistsException
     * @throws DateException      DateException
     * @throws NotFoundException  NotFoundException
     * @throws AuthorityException AuthorityException
     */
    @Test
    public void registerBasicUserTest()
            throws ExistsException, DateException, NotFoundException, AuthorityException {

        // act
        User basicUser = authJpaService.register("A3", test, "live@gmail.com",
                false);

        // assert
        assertThat(basicUser).isNotNull();
        assertThat(adminJpaService.count()).isEqualTo(1);
        assertThat(basicUserJpaService.count()).isEqualTo(2);
    }

    /**
     * Register a basic user that exists.
     *
     * @throws ExistsException    ExistsException
     * @throws DateException      DateException
     * @throws NotFoundException  NotFoundException
     * @throws AuthorityException AuthorityException
     */
    @Test
    public void registerBasicUserAlreadyFoundTest()
            throws ExistsException, DateException, NotFoundException, AuthorityException {

        // act
        User basicUser = authJpaService.register(userId, test, "live@gmail.com",
                false);

        // assert
        assertThat(basicUser).isNotNull();
        assertThat(adminJpaService.count()).isEqualTo(1);
        assertThat(basicUserJpaService.count()).isEqualTo(1);
    }

    /**
     * Find by email.
     *
     * @throws NotFoundException NotFoundException
     */
    @Test
    public void findByEmailTest() throws NotFoundException {

        // act
        UserDto userDto = authJpaService.findByEmail("gafe@gmail.com");

        // assert
        assertThat(userDto.getId()).isEqualTo(userId);
    }

    /**
     * Find by email null test.
     */
    @Test
    public void findByEmailNullTest() {

        // act and assert
        assertThrows(NotFoundException.class, () -> {
            authJpaService.findByEmail("akash@gmail.com");
        });
    }


    /**
     * Admin is always validated.
     */
    @Test
    public void verifyAdminTest() {

        // act and assert
        assertDoesNotThrow(() -> {
            authJpaService.validate(token1, "23", "45");
        });
    }


    /**
     * UserId is null Test.
     */
    @Test
    public void verifyUserIdNullTest() {

        // act and assert
        assertThrows(NotFoundException.class, () -> {
            authJpaService.validate("we", "ew", null);
        });
    }

    /**
     * BadWeather Test.
     */
    @Test
    public void isNotAdminAndUserIdIsNotEqualToOidTest() {

        // act and assert
        assertThrows(AuthorityException.class, () -> {
            authJpaService.validate(token2, "23", "57");
        });
    }

    /**
     * Edge case test.
     */
    @Test
    public void isAdminAndUserIdIsNotEqualToOidTest() {

        // act and assert
        assertDoesNotThrow(() -> {
            authJpaService.validate(token1, "23", "23");
        });
    }
}
