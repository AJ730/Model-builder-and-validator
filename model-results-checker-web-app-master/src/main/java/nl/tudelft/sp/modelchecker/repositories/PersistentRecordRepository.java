package nl.tudelft.sp.modelchecker.repositories;

import nl.tudelft.sp.modelchecker.entities.PersistentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersistentRecordRepository extends JpaRepository<PersistentRecord, Long> {
}
