package nl.tudelft.sp.modelchecker.repositories;


import nl.tudelft.sp.modelchecker.entities.Csv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CsvRepository extends JpaRepository<Csv, Long> {
}
