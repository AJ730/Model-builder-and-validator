package nl.tudelft.sp.modelchecker.services.servicebeans;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javassist.NotFoundException;
import nl.tudelft.sp.modelchecker.dto.ContainerDto;
import nl.tudelft.sp.modelchecker.dto.CsvDto;
import nl.tudelft.sp.modelchecker.dto.RecordDto;
import nl.tudelft.sp.modelchecker.entities.Container;
import nl.tudelft.sp.modelchecker.entities.PersistentCsv;
import nl.tudelft.sp.modelchecker.entities.PersistentRecord;
import nl.tudelft.sp.modelchecker.exceptions.ExistsException;
import nl.tudelft.sp.modelchecker.services.PersistentCsvService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PersistentCsvJpaService extends CrudJpaService<PersistentCsv, Long, CsvDto>
        implements PersistentCsvService {


    @Autowired
    private ContainerJpaService containerJpaService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PersistentRecordJpaService persistentRecordJpaService;


    /**
     * Initialize a CrudJpaRepository.
     *
     * @param repository repository
     */
    public PersistentCsvJpaService(JpaRepository<PersistentCsv, Long> repository) {
        super(repository);
    }

    /**
     * Register a persistent csv.
     *
     * @param persistentCsv persistentCsv
     * @param containerDto  containerDto
     * @return persistent csv
     * @throws ExistsException                          ExistsException
     * @throws NotFoundException                        NotFoundException
     * @throws SQLIntegrityConstraintViolationException SQLIntegrityConstraintViolationException
     */
    @Override
    @Transactional(rollbackFor = {Exception.class},
            propagation = Propagation.REQUIRED)
    public PersistentCsv register(PersistentCsv persistentCsv, ContainerDto containerDto)
            throws ExistsException, NotFoundException, SQLIntegrityConstraintViolationException {

        if (exists(persistentCsv)) throw new ExistsException("CSV already exists");

        if (!containerJpaService.exists(containerDto))
            throw new NotFoundException("Container not found");

        Container container = containerJpaService.findById(containerDto.getId());

        if (container.getPersistentCSv() != null) {
            throw new SQLIntegrityConstraintViolationException(
                    "CSv cannot be part of multiple containers");
        }

        container.setPersistentCSv(persistentCsv);
        persistentCsv.setContainer(container);

        return save(persistentCsv);
    }

    /**
     * Get records from a persistent csv.
     *
     * @param csvDto csvDto
     * @return records
     * @throws NotFoundException NotFoundException
     */
    @Override
    @Transactional(rollbackFor = {Exception.class},
            propagation = Propagation.REQUIRED)
    public List<RecordDto> getRecordsInPersistentCsv(CsvDto csvDto) throws NotFoundException {

        PersistentCsv persistentCsv = findById(csvDto.getId());

        if (persistentCsv == null) throw new NotFoundException("Csv not found");

        Set<PersistentRecord> records = persistentCsv.getPersistentRecords();

        return records.stream().map(n -> modelMapper.map(n, RecordDto.class)).sorted()
                .collect(Collectors.toList());
    }

    /**
     * Update records of a dto.
     *
     * @param oldDto oldDto
     * @param newDto newDto
     * @return UnsupportedOperationException
     * @throws NotFoundException NotFoundException
     */
    @Override
    public PersistentCsv update(CsvDto oldDto, CsvDto newDto) throws NotFoundException {
        throw new UnsupportedOperationException("Persistent Csv is read-only");
    }

    /**
     * Save a csv to a container.
     *
     * @param containerDto containerDto
     * @param csvDto       csvDto
     * @return saved csv
     * @throws Exception Exception
     */
    @Transactional(rollbackFor = {Exception.class},
            propagation = Propagation.REQUIRED)
    public PersistentCsv saveCsv(ContainerDto containerDto, CsvDto csvDto)
            throws Exception {

        PersistentCsv persistentCsv = register(new PersistentCsv(), containerDto);

        CsvDto persistentDto = new CsvDto(persistentCsv);
        persistentRecordJpaService.save(csvDto, persistentDto);

        return persistentCsv;
    }
}
