package nl.tudelft.sp.modelchecker.services.servicebeans;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javassist.NotFoundException;
import javax.validation.constraints.NotNull;
import nl.tudelft.sp.modelchecker.dto.ContainerDto;
import nl.tudelft.sp.modelchecker.dto.CsvDto;
import nl.tudelft.sp.modelchecker.dto.RecordDto;
import nl.tudelft.sp.modelchecker.entities.Container;
import nl.tudelft.sp.modelchecker.entities.Csv;
import nl.tudelft.sp.modelchecker.entities.Record;
import nl.tudelft.sp.modelchecker.exceptions.ExistsException;
import nl.tudelft.sp.modelchecker.services.CsvService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
public class CsvJpaService extends CrudJpaService<Csv, Long, CsvDto> implements CsvService {


    @Autowired
    private ContainerJpaService containerJpaService;

    @Autowired
    private RecordJpaService recordJpaService;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Initialize a Csv repository.
     *
     * @param repository repository
     */
    public CsvJpaService(JpaRepository<Csv, Long> repository) {
        super(repository);
    }

    /**
     * Update a CSv.(Not Supported)
     *
     * @param oldDto oldDto
     * @param newDto newDto
     * @return updated Csv
     */
    @Override
    public Csv update(CsvDto oldDto, CsvDto newDto) {
        throw new UnsupportedOperationException();
    }

    /**
     * Register a csv with a container.
     *
     * @param csv          csv
     * @param containerDto containerDto
     * @return registered Csv
     * @throws ExistsException                          ExistsException
     * @throws NotFoundException                        NotFoundException
     * @throws SQLIntegrityConstraintViolationException SQLIntegrityConstraintViolationException
     */
    @Transactional(rollbackFor = {Exception.class},
            propagation = Propagation.REQUIRED)
    @Override
    public Csv register(@NotNull Csv csv, @NotNull ContainerDto containerDto)
            throws ExistsException, NotFoundException, SQLIntegrityConstraintViolationException {

        if (exists(csv)) throw new ExistsException("CSV already exists");

        if (!containerJpaService.exists(containerDto))
            throw new NotFoundException("Container not found");

        Container container = containerJpaService.findById(containerDto.getId());

        if (container.getCsv() != null) {
            throw new SQLIntegrityConstraintViolationException(
                    "CSv cannot be part of multiple containers");
        }

        container.setCsv(csv);
        csv.setContainer(container);

        return save(csv);
    }

    /**
     * Get records in a csv.
     *
     * @param csvDto csvDto
     * @return records
     * @throws NotFoundException NotFoundException
     */
    @Override
    public List<RecordDto> getRecordsInCsv(CsvDto csvDto) throws NotFoundException {

        Csv csv = findById(csvDto.getId());

        if (csv == null) throw new NotFoundException("Csv not found");

        Set<Record> records = csv.getRecords();

        return records.stream().map(n -> modelMapper.map(n, RecordDto.class)).sorted()
                .collect(Collectors.toList());
    }

    /**
     * Delete records in a csv.
     *
     * @param csvDto csvDto
     * @return deleted records
     * @throws NotFoundException NotFoundException
     */
    @Transactional(rollbackFor = {Exception.class},
            propagation = Propagation.REQUIRED)
    @Override
    public Csv deleteRecordsInCsv(CsvDto csvDto) throws NotFoundException {
        Csv csv = findById(csvDto.getId());
        if (csv == null) throw new NotFoundException("Csv not found");

        recordJpaService.deleteAll(csv.getRecords());

        return findById(csvDto.getId());
    }


    /**
     * Create and save records.
     *
     * @param containerDto     containerDto
     * @param multipartFileCsv multipartFileCsv
     * @return saved csv
     * @throws Exception Exception
     */
    @Transactional(rollbackFor = {Exception.class},
            propagation = Propagation.REQUIRED)
    public Csv createCsvAndSaveRecords(ContainerDto containerDto, MultipartFile multipartFileCsv)
            throws Exception {
        Csv returnCsv = register(new Csv(), containerDto);
        CsvDto csvDto = new CsvDto(returnCsv);
        recordJpaService.save(multipartFileCsv, csvDto);

        return returnCsv;
    }
}