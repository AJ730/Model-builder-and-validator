package nl.tudelft.sp.modelchecker.services.servicebeans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javassist.NotFoundException;
import javax.validation.constraints.NotNull;
import nl.tudelft.sp.modelchecker.dto.CsvDto;
import nl.tudelft.sp.modelchecker.dto.RecordDto;
import nl.tudelft.sp.modelchecker.entities.Csv;
import nl.tudelft.sp.modelchecker.entities.Record;
import nl.tudelft.sp.modelchecker.exceptions.ExistsException;
import nl.tudelft.sp.modelchecker.parsers.CsvParser.Adapter;
import nl.tudelft.sp.modelchecker.repositories.RecordRepository;
import nl.tudelft.sp.modelchecker.services.RecordService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
public class RecordJpaService extends CrudJpaService<Record, Long, RecordDto>
        implements RecordService {


    private final RecordRepository recordRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    private CsvJpaService csvjpaService;


    /**
     * Initialize a repository.
     *
     * @param repository       repository
     * @param recordRepository recordRepository
     */
    public RecordJpaService(JpaRepository<Record, Long> repository,
                            RecordRepository recordRepository) {
        super(repository);
        this.recordRepository = recordRepository;
    }


    /**
     * Register a new record, with a csv.
     *
     * @param record record
     * @param csvdto csvdto
     * @return registered Record.
     * @throws ExistsException   ExistsException
     * @throws NotFoundException NotFoundException
     */
    @Transactional(rollbackFor = {Exception.class},
            propagation = Propagation.REQUIRED)
    @Override
    public Record register(@NotNull Record record, @NotNull CsvDto csvdto)
            throws ExistsException, NotFoundException {

        if (exists(record)) throw new ExistsException("Record already exists");

        if (!csvjpaService.exists(csvdto)) throw new NotFoundException("CSV not found");

        Csv csv = csvjpaService.findById(csvdto.getId());
        csv.getRecords().add(record);
        record.setCsv(csv);


        return save(record);
    }

    /**
     * Update a record.
     *
     * @param oldDto dto with the id of the old entity
     * @param newDto dto with the parameters to change
     * @return updated Record
     * @throws NotFoundException NotFoundException
     */
    @Transactional(rollbackFor = {Exception.class},
            propagation = Propagation.REQUIRED)
    @Override
    public Record update(@NotNull RecordDto oldDto, @NotNull RecordDto newDto)
            throws NotFoundException {


        Csv csv = csvjpaService.findById(oldDto.getCsvId());
        int objectId = newDto.getObjectId();

        if (!exists(oldDto) & !recordRepository.existsRecordByCsvAndObjectId(csv, objectId)) {
            throw new NotFoundException("Record does not exist");
        }

        Record record = recordRepository.findRecordByCsvAndObjectId(csv, objectId);

        record.updateAttributes(newDto);

        return save(record);
    }

    /**
     * Save a multipart file.
     *
     * @param file file
     * @throws IOException       IOException
     * @throws ExistsException   ExistsException
     * @throws NotFoundException NotFoundException
     */
    @Transactional(rollbackFor = {Exception.class},
            propagation = Propagation.REQUIRED)
    @Override
    public void save(@NotNull MultipartFile file, @NotNull CsvDto csvdto)
            throws IOException, ExistsException, NotFoundException {

        overrideCsvCheck(csvdto);

        Adapter adapter = new Adapter(file);

        List<Record> records = adapter.getRecords(file);
        List<Record> detachedRecords = new ArrayList<>();
        Csv csv = csvjpaService.findById(csvdto.getId());

        for (Record record : records) {
            csv.getRecords().add(record);
            record.setCsv(csv);
            detachedRecords.add(record);
        }

        saveAll(detachedRecords);
    }

    @Transactional(rollbackFor = {Exception.class},
            propagation = Propagation.REQUIRED)
    @Override
    public void save(@NotNull List<RecordDto> recordDtos, @NotNull CsvDto csvdto)
            throws IOException, ExistsException, NotFoundException {

        List<Record> records = changeRecordDtosToRecord(recordDtos);

        List<Record> updatedRecords = new ArrayList<>();
        List<Record> registeredRecords = new ArrayList<>();

        for (Record record : records) {
            Csv csv = record.getCsv();
            int objectId = record.getObjectId();

            if (recordRepository.existsRecordByCsvAndObjectId(csv, objectId)) {
                Record existingRecord = recordRepository.findRecordByCsvAndObjectId(csv, objectId);
                existingRecord.updateAttributes(modelMapper.map(record, RecordDto.class));
                updatedRecords.add(existingRecord);
            } else {
                csv = csvjpaService.findById(csvdto.getId());
                record.setCsv(csv);
                csv.getRecords().add(record);
                registeredRecords.add(record);
            }
        }
        saveAll(updatedRecords);
        saveAll(registeredRecords);
    }

    @Transactional(rollbackFor = {Exception.class},
            propagation = Propagation.REQUIRED)
    @Override
    public void delete(List<RecordDto> recordDtos) {
        List<Record> recordList = new ArrayList<>();

        for (RecordDto recordDto : recordDtos) {
            Csv csv = csvjpaService.findById(recordDto.getCsvId());
            int objectId = recordDto.getObjectId();

            if (recordRepository.existsRecordByCsvAndObjectId(csv, objectId)) {
                Record record = recordRepository.findRecordByCsvAndObjectId(csv, objectId);
                recordList.add(record);
            }
        }

        deleteAll(recordList);
    }

    /**
     * Delete all current records in a csv.
     *
     * @param csvdto csvdto
     * @throws NotFoundException NotFoundException
     */
    @Transactional(rollbackFor = {Exception.class},
            propagation = Propagation.REQUIRED)
    public void overrideCsvCheck(CsvDto csvdto) throws NotFoundException {
        csvjpaService.deleteRecordsInCsv(csvdto);
    }

    /**
     * convert recordDtos into records.
     *
     * @param recordDtos recordDtos
     * @return list of records.
     */
    public List<Record> changeRecordDtosToRecord(List<RecordDto> recordDtos) {
        return recordDtos.stream()
                .map(a -> modelMapper.map(a, Record.class))
                .collect(Collectors.toList());
    }

}
