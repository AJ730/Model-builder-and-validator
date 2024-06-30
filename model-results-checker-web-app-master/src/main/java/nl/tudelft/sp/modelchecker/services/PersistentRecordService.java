package nl.tudelft.sp.modelchecker.services;

import java.io.IOException;
import javassist.NotFoundException;
import nl.tudelft.sp.modelchecker.dto.CsvDto;
import nl.tudelft.sp.modelchecker.dto.RecordDto;
import nl.tudelft.sp.modelchecker.entities.PersistentRecord;
import nl.tudelft.sp.modelchecker.exceptions.ExistsException;

public interface PersistentRecordService extends CrudService<PersistentRecord, Long, RecordDto> {

    /**
     * Register a record with CSv.
     *
     * @param record record
     * @param csvdto csvdto
     * @return Record
     * @throws ExistsException   ExistsException
     * @throws NotFoundException NotFoundException
     */
    PersistentRecord register(PersistentRecord record, CsvDto csvdto)
            throws ExistsException, NotFoundException;

    /**
     * Save persistent csv using normal csv.
     *
     * @param csvNormal     csvNormal
     * @param csvPersistent csvPersistent
     * @throws IOException       IOException
     * @throws ExistsException   ExistsException
     * @throws NotFoundException NotFoundException
     */
    void save(CsvDto csvNormal, CsvDto csvPersistent)
            throws IOException, ExistsException, NotFoundException;

}
