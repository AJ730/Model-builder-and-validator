package nl.tudelft.sp.modelchecker.repositories;

import nl.tudelft.sp.modelchecker.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
}
