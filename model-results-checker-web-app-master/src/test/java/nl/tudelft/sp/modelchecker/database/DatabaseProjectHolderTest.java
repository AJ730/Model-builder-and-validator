package nl.tudelft.sp.modelchecker.database;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.util.List;
import javassist.NotFoundException;
import nl.tudelft.sp.modelchecker.Application;
import nl.tudelft.sp.modelchecker.dto.*;
import nl.tudelft.sp.modelchecker.entities.*;
import nl.tudelft.sp.modelchecker.exceptions.AuthorityException;
import nl.tudelft.sp.modelchecker.exceptions.DateException;
import nl.tudelft.sp.modelchecker.exceptions.ExistsException;
import nl.tudelft.sp.modelchecker.services.servicebeans.*;
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
public class DatabaseProjectHolderTest {

    @Autowired
    ProjectHolderJpaService projectHolderJpaService;

    @Autowired
    BasicUserJpaService basicUserJpaService;

    @Autowired
    AdminJpaService adminJpaService;

    @Autowired
    ProjectJpaService projectJpaService;

    @Autowired
    ContainerJpaService containerJpaService;

    @Autowired
    CsvJpaService csvJpaService;

    @Autowired
    ModelMapper modelMapper;

    private BasicUser client;
    private UserDto clientDto;

    private Admin admin;
    private UserDto adminDto;

    private Container container;

    private ContainerDto containerDto;

    private Project project;
    private ProjectDto projectDto;

    private ProjectHolder projectHolder;
    private ProjectHolderDto projectHolderDto;

    private ProjectHolder projectHolder2;
    private ProjectHolderDto projectHolderDto2;

    private Csv csv;
    private CsvDto csvdto;


    /**
     * SetUp Test environment.
     *
     * @throws ExistsException                          ExistsException
     * @throws NotFoundException                        NotFoundException
     * @throws SQLIntegrityConstraintViolationException SQLIntegrityConstraintViolationException
     * @throws IOException                              IOException
     * @throws AuthorityException                       AuthorityException
     */
    @BeforeEach
    public void setUp() throws ExistsException,
            NotFoundException, SQLIntegrityConstraintViolationException,
            IOException, AuthorityException, DateException {

        // arrange
        admin = new Admin("1A", "akash", "amalan_akash@hotmail.com",
                Date.valueOf(LocalDate.now()));

        client = new BasicUser("2A", "dean", "dean_nyugen@hotmail.com",
                Date.valueOf(LocalDate.now()));

        admin = adminJpaService.registerUser(admin);
        client = basicUserJpaService.registerUser(client);

        adminDto = modelMapper.map(admin, UserDto.class);
        clientDto = modelMapper.map(client, UserDto.class);

        projectHolder = projectHolderJpaService.register(new ProjectHolder(), clientDto);
        projectHolderDto = modelMapper.map(projectHolder, ProjectHolderDto.class);

        projectHolder2 = new ProjectHolder();
        projectHolderDto2 = modelMapper.map(projectHolder2, ProjectHolderDto.class);

        project = projectJpaService.register(new Project("title", "description"),
                projectHolderDto, adminDto);
        projectDto = modelMapper.map(project, ProjectDto.class);

        container = containerJpaService.register(
                new Container(42.0, "test", "testContainer"), projectDto);
        containerDto = modelMapper.map(container, ContainerDto.class);

        csv = csvJpaService.register(new Csv(), containerDto);
        csvdto = modelMapper.map(csv, CsvDto.class);
    }

    /**
     * Register Project Holder.
     */
    @Test
    public void registerProjectHolderTest() {

        // assert
        assertThat(projectHolderJpaService.count()).isEqualTo(1);
        projectHolder = projectHolderJpaService.findById(projectHolder.getId());
        admin = adminJpaService.findById(admin.getId());

        assertThat(projectHolder.getProjects().toArray()[0]).isEqualTo(project);
        assertThat(project.getProjectHolder()).isEqualTo(projectHolder);
    }

    /**
     * test register 2 project holders.
     *
     * @throws ExistsException ExistsException
     */
    @Test
    public void register2HoldersTest() throws ExistsException {

        // act
        projectHolderJpaService.register(projectHolder2);

        // assert
        assertThat(projectHolderJpaService.count()).isEqualTo(2);
    }

    /**
     * Register unknown project.
     */
    @Test
    public void registerClientNotFoundProjectTest() {

        // act and assert
        assertThrows(NotFoundException.class, () -> {
            projectHolderJpaService.register(projectHolder2, new UserDto("3A"));
        });
    }

    /**
     * Register already existing project.
     */
    @Test
    public void registerExitingProjectHolderTest() {

        // act and assert
        assertThrows(ExistsException.class, () -> {
            projectHolderJpaService.register(projectHolder, adminDto);
        });
    }

    /**
     * test register user project.
     */
    @Test
    public void registerUserProjectTest() {

        // arrange
        client = basicUserJpaService.findById(client.getId());

        // assert
        assertThat(projectHolder.getClient()).isEqualTo(client);
        assertThat(client.getProjectHolder()).isEqualTo(projectHolder);
    }

    /**
     * bad whether test.
     */
    @Test
    public void registerAdminBadWeather() {

        // act and assert
        assertThrows(AuthorityException.class, () -> {
            projectHolderJpaService.register(projectHolder2, adminDto);
        });
    }

    /**
     * test get projectdtos of nonexistent project holder.
     */
    @Test
    public void getProjectDtosProjectHolderNull() {

        // arrange
        ProjectHolderDto projectHolderReturnDto = new ProjectHolderDto(10000L);

        // act and assert
        assertThrows(NotFoundException.class, () -> {
            projectHolderJpaService.getProjectDtosInProjectHolder(projectHolderReturnDto);
        });
    }

    /**
     * test get project dtos in project holder.
     *
     * @throws NotFoundException NotFoundException
     * @throws ExistsException   ExistsException
     */
    @Test
    public void getProjectDtosInProjectHolder() throws NotFoundException, ExistsException {

        // arrange
        projectJpaService.register(
                new Project("title", "description"),
                projectHolderDto, adminDto);

        // act
        List<ProjectDto> projectDtos = projectHolderJpaService
                .getProjectDtosInProjectHolder(projectHolderDto);

        // assert
        assertThat(projectDtos.size()).isEqualTo(2);
    }


    /**
     * Update is not supported.
     */
    @Test
    public void updateIsNotSupportedTest() {

        // act and assert
        assertThrows(UnsupportedOperationException.class, () -> {
            projectHolderJpaService.update(projectHolderDto, projectHolderDto2);
        });
    }

    /**
     * Delete project Holder.
     *
     * @throws NotFoundException NotFoundException
     */
    @Test
    public void deleteProjectHolderTrickleDownTest() throws NotFoundException {

        // act
        projectHolderJpaService.delete(projectHolderDto);

        // assert
        assertThat(projectHolderJpaService.count()).isEqualTo(0);
        assertThat(projectJpaService.count()).isEqualTo(0);
        assertThat(containerJpaService.count()).isEqualTo(0);
        assertThat(csvJpaService.count()).isEqualTo(0);
        assertThat(basicUserJpaService.count()).isEqualTo(1);
        assertThat(adminJpaService.count()).isEqualTo(1);
    }

    /**
     * Delete admin.
     *
     * @throws NotFoundException NotFoundException
     */
    @Test
    public void deleteAdminTrickleDownTest() throws NotFoundException {

        // act
        adminJpaService.delete(adminDto);

        // assert
        assertThat(projectHolderJpaService.count()).isEqualTo(1);
        assertThat(projectJpaService.count()).isEqualTo(0);
        assertThat(containerJpaService.count()).isEqualTo(0);
        assertThat(csvJpaService.count()).isEqualTo(0);

    }

    /**
     * Delete client.
     *
     * @throws NotFoundException NotFoundException
     */
    @Test
    public void deleteClientDeleteEverythingTest() throws NotFoundException {

        // act
        adminJpaService.delete(clientDto);

        // assert
        assertThat(projectHolderJpaService.count()).isEqualTo(0);

        assertThat(projectJpaService.count()).isEqualTo(0);
        assertThat(containerJpaService.count()).isEqualTo(0);
        assertThat(csvJpaService.count()).isEqualTo(0);
        assertThat(basicUserJpaService.count()).isEqualTo(0);
        assertThat(adminJpaService.count()).isEqualTo(1);
    }

    /**
     * Delete without preRemove.
     */
    @Test
    public void deleteStandAloneProject() throws ExistsException, NotFoundException {

        // arrange
        ProjectHolder standAlone = projectHolderJpaService.register(new ProjectHolder());

        // act
        projectHolderJpaService.deleteById(standAlone.getId());

        // assert
        assertThat(projectHolderJpaService.count()).isEqualTo(1);
    }
}
