package nl.tudelft.sp.modelchecker.services;

import java.io.IOException;
import java.util.List;
import javassist.NotFoundException;
import nl.tudelft.sp.modelchecker.dto.CsvDto;
import nl.tudelft.sp.modelchecker.dto.RecordDto;
import nl.tudelft.sp.modelchecker.entities.Record;
import nl.tudelft.sp.modelchecker.exceptions.ExistsException;
import org.springframework.web.multipart.MultipartFile;


public interface RecordService extends CrudService<Record, Long, RecordDto> {

    /**
     * Register a record with CSv.
     *
     * @param record record
     * @param csvdto csvdto
     * @return Record
     * @throws ExistsException   ExistsException
     * @throws NotFoundException NotFoundException
     */
    Record register(Record record, CsvDto csvdto)
            throws ExistsException, NotFoundException;

    /**
     * Save a multipartFile in dto.
     *
     * @param file   file
     * @param csvdto csvdto
     * @throws IOException       IOException
     * @throws ExistsException   ExistsException
     * @throws NotFoundException NotFoundException
     */
    void save(MultipartFile file, CsvDto csvdto)
            throws IOException, ExistsException, NotFoundException;

    /**
     * save records.
     *
     * @param recordDtoList recordDtoList
     * @param csvdto        csvdto
     * @throws IOException       IOException
     * @throws ExistsException   ExistsException
     * @throws NotFoundException NotFoundException
     */
    void save(List<RecordDto> recordDtoList, CsvDto csvdto)
            throws IOException, ExistsException, NotFoundException;

    /**
     * delete records in csv file.
     *
     * @param recordDtos recordDtos
     */
    void delete(List<RecordDto> recordDtos);

}
