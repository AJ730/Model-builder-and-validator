package nl.tudelft.sp.modelchecker.services;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import javassist.NotFoundException;
import nl.tudelft.sp.modelchecker.dto.ContainerDto;
import nl.tudelft.sp.modelchecker.dto.CsvDto;
import nl.tudelft.sp.modelchecker.dto.RecordDto;
import nl.tudelft.sp.modelchecker.entities.Csv;
import nl.tudelft.sp.modelchecker.exceptions.ExistsException;
import org.springframework.web.multipart.MultipartFile;


public interface CsvService extends CrudService<Csv, Long, CsvDto> {

    /**
     * Register a csv with a container.
     *
     * @param csv          csv
     * @param containerDto containerDto
     * @return CSv
     * @throws ExistsException                          ExistsException
     * @throws NotFoundException                        NotFoundException
     * @throws SQLIntegrityConstraintViolationException SQLIntegrityConstraintViolationException
     */
    Csv register(Csv csv, ContainerDto containerDto)
            throws ExistsException,
            NotFoundException,
            SQLIntegrityConstraintViolationException;

    /**
     * Get records in a csv.
     *
     * @param csvDto csvDto
     * @return records
     * @throws NotFoundException NotFoundException
     */
    List<RecordDto> getRecordsInCsv(CsvDto csvDto) throws NotFoundException;

    /**
     * Delete records in a csv.
     *
     * @param csvDto csvDto
     * @return deleted records
     * @throws NotFoundException NotFoundException
     * @throws ExistsException   ExistsException
     */
    Csv deleteRecordsInCsv(CsvDto csvDto) throws NotFoundException, ExistsException;

    /**
     * Create and save records.
     *
     * @param containerDto     containerDto
     * @param multipartFileCsv multipartFileCsv
     * @return saved records.
     * @throws Exception Exception
     */
    Csv createCsvAndSaveRecords(ContainerDto containerDto, MultipartFile multipartFileCsv)
            throws Exception;


}
