package nl.tudelft.sp.modelchecker.repositories;

import nl.tudelft.sp.modelchecker.entities.ProjectHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectHolderRepository extends JpaRepository<ProjectHolder, Long> {
}
