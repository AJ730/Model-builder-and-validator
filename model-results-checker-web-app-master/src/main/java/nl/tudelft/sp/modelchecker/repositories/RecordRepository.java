package nl.tudelft.sp.modelchecker.repositories;


import java.util.List;
import nl.tudelft.sp.modelchecker.entities.Csv;
import nl.tudelft.sp.modelchecker.entities.Record;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {

    @Cacheable(value = "entities")
    @Override
    <S extends Record> List<S> saveAll(Iterable<S> entities);


    /**
     * check if the record exist by using csv and objectId.
     *
     * @param csv      csv
     * @param objectId objectId
     * @return true if exist otherwise false
     */
    boolean existsRecordByCsvAndObjectId(Csv csv, int objectId);

    /**
     * find a record by using csv and objectId.
     *
     * @param csv      csv
     * @param objectId objectId
     * @return Record
     */
    Record findRecordByCsvAndObjectId(Csv csv, int objectId);
}

