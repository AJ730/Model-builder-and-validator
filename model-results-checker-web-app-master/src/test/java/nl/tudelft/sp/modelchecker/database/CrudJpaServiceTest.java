package nl.tudelft.sp.modelchecker.database;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import javassist.NotFoundException;
import nl.tudelft.sp.modelchecker.dto.ProjectDto;
import nl.tudelft.sp.modelchecker.repositories.ProjectRepository;
import nl.tudelft.sp.modelchecker.services.servicebeans.ProjectJpaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CrudJpaServiceTest {

    @Mock
    ProjectRepository projectRepository;

    @InjectMocks
    ProjectJpaService projectJpaService;

    @Test
    void retrieveNullDtos() {
        when(projectRepository.findAll()).thenReturn(null);
        assertThrows(NotFoundException.class, () -> {
            projectJpaService.findAllDtos(ProjectDto.class);
        });
    }
}
