package nl.tudelft.sp.modelchecker.services.servicebeans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javassist.NotFoundException;
import nl.tudelft.sp.modelchecker.dto.CsvDto;
import nl.tudelft.sp.modelchecker.dto.RecordDto;
import nl.tudelft.sp.modelchecker.entities.Csv;
import nl.tudelft.sp.modelchecker.entities.PersistentCsv;
import nl.tudelft.sp.modelchecker.entities.PersistentRecord;
import nl.tudelft.sp.modelchecker.entities.Record;
import nl.tudelft.sp.modelchecker.exceptions.ExistsException;
import nl.tudelft.sp.modelchecker.services.PersistentRecordService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PersistentRecordJpaService extends CrudJpaService<PersistentRecord, Long, RecordDto>
        implements PersistentRecordService {


    @Autowired
    private PersistentCsvJpaService persistentCsvJpaService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CsvJpaService csvJpaService;

    /**
     * Initialize a CrudJpaRepository.
     *
     * @param repository repository
     */
    public PersistentRecordJpaService(JpaRepository<PersistentRecord, Long> repository) {
        super(repository);
    }

    /**
     * Register a persistent record.
     *
     * @param record record
     * @param csvdto csvdto
     * @return persistent record
     * @throws ExistsException   ExistsException
     * @throws NotFoundException NotFoundException
     */
    @Override
    @Transactional(rollbackFor = {Exception.class},
            propagation = Propagation.REQUIRED)
    public PersistentRecord register(PersistentRecord record, CsvDto csvdto) throws ExistsException,
            NotFoundException {

        if (exists(record)) throw new ExistsException("Record already exists");

        if (!persistentCsvJpaService.exists(csvdto)) throw new NotFoundException("CSV not found");

        PersistentCsv csv = persistentCsvJpaService.findById(csvdto.getId());
        csv.getPersistentRecords().add(record);
        record.setPersistentCsv(csv);

        return save(record);
    }

    /**
     * Save a persistent csv from normal csv.
     *
     * @param csvNormal     csvNormal
     * @param csvPersistent csvPersistent
     * @throws IOException       IOException
     * @throws ExistsException   ExistsException
     * @throws NotFoundException NotFoundException
     */
    @Transactional(rollbackFor = {Exception.class},
            propagation = Propagation.REQUIRED)
    @Override
    public void save(CsvDto csvNormal, CsvDto csvPersistent)
            throws IOException, ExistsException, NotFoundException {

        Csv csv = csvJpaService.findById(csvNormal.getId());
        PersistentCsv persistentCsv = persistentCsvJpaService.findById(csvPersistent.getId());
        List<PersistentRecord> detachedRecords = new ArrayList<>();

        for (Record record : csv.getRecords()) {
            PersistentRecord persistentRecord = modelMapper.map(record, PersistentRecord.class);
            persistentRecord.setId(null);
            persistentRecord.setPersistentCsv(persistentCsv);
            persistentCsv.getPersistentRecords().add(persistentRecord);
            detachedRecords.add(persistentRecord);
        }

        saveAll(detachedRecords);
    }

    /**
     * Update a recordDto.
     *
     * @param oldDto oldDto
     * @param newDto newDto
     * @return updated record
     * @throws NotFoundException NotFoundException
     */
    @Transactional(rollbackFor = {Exception.class},
            propagation = Propagation.REQUIRED)
    @Override
    public PersistentRecord update(RecordDto oldDto, RecordDto newDto) throws NotFoundException {

        if (!exists(oldDto)) throw new NotFoundException("Record does not exist");

        PersistentRecord persistentRecord = findById(oldDto.getId());

        persistentRecord.updateAttributes(newDto);

        return save(persistentRecord);
    }
}
