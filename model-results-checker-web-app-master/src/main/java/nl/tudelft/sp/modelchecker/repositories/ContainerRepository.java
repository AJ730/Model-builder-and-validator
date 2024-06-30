package nl.tudelft.sp.modelchecker.repositories;

import nl.tudelft.sp.modelchecker.entities.Container;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContainerRepository extends JpaRepository<Container, Long> {
}
