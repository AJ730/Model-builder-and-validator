package nl.tudelft.sp.modelchecker.parser;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Scanner;
import javassist.NotFoundException;
import nl.tudelft.sp.modelchecker.Application;
import nl.tudelft.sp.modelchecker.entities.Admin;
import nl.tudelft.sp.modelchecker.entities.BasicUser;
import nl.tudelft.sp.modelchecker.entities.User;
import nl.tudelft.sp.modelchecker.exceptions.AuthorityException;
import nl.tudelft.sp.modelchecker.exceptions.DateException;
import nl.tudelft.sp.modelchecker.exceptions.ExistsException;
import nl.tudelft.sp.modelchecker.services.servicebeans.AdminJpaService;
import nl.tudelft.sp.modelchecker.services.servicebeans.BasicUserJpaService;
import nl.tudelft.sp.modelchecker.services.servicebeans.JwtTokenJpaService;
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
public class JwtTokenParserTest {

    @Autowired
    JwtTokenJpaService jwtTokenJpaService;
    @Autowired
    ProjectHolderJpaService projectHolderJpaService;
    @Autowired
    BasicUserJpaService basicUserJpaService;
    @Autowired
    AdminJpaService adminJpaService;
    private String token1;
    private String token2;

    /**
     * Read resources for testing.
     *
     * @throws FileNotFoundException FileNotFoundException
     */
    @BeforeEach
    public void setUp() throws FileNotFoundException {

        // arrange
        File file = new File("src/test/resources/Tokens.txt");
        FileReader fr = new FileReader(file);
        Scanner sc = new Scanner(fr);
        token1 = sc.nextLine();
        token2 = sc.nextLine();
        sc.close();
    }

    /**
     * Test whether the tokenParser parses an admin properly.
     */
    @Test
    public void testTokenTanAdminParse() throws DateException, ExistsException,
            NotFoundException, AuthorityException {

        // act
        User tan = jwtTokenJpaService.parseToken(token1);

        // assert
        assertThat(tan.getEmail()).isEqualTo("tandao311@gmail.com");
        assertThat(tan.getUsername()).isEqualTo("Tan dao");
        assertThat(tan.getRegistrationDate()).isEqualTo(Date.valueOf(LocalDate.now()));
        assertThat(tan).isExactlyInstanceOf(Admin.class);
        assertThat(adminJpaService.findById(tan.getId())).isNotNull();
        assertThat(adminJpaService.count()).isEqualTo(1);
    }

    /**
     * Test whether the tokenParser parses a user properly.
     */
    @Test
    public void testTokenThangUserParse() throws ExistsException, DateException,
            NotFoundException, AuthorityException {

        // act
        User thang = jwtTokenJpaService.parseToken(token2);

        // assert
        assertThat(thang.getEmail()).isEqualTo("buinamthang1992@outlook.com");
        assertThat(thang.getUsername()).isEqualTo("thang");
        assertThat(thang.getRegistrationDate()).isEqualTo(Date.valueOf(LocalDate.now()));
        assertThat(thang).isExactlyInstanceOf(BasicUser.class);
        assertThat(basicUserJpaService.findById(thang.getId())).isNotNull();
        assertThat(basicUserJpaService.count()).isEqualTo(1);
        assertThat(projectHolderJpaService.count()).isEqualTo(1);
    }
}
