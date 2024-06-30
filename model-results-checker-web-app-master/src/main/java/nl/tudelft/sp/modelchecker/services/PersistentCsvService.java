package nl.tudelft.sp.modelchecker.services;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import javassist.NotFoundException;
import nl.tudelft.sp.modelchecker.dto.ContainerDto;
import nl.tudelft.sp.modelchecker.dto.CsvDto;
import nl.tudelft.sp.modelchecker.dto.RecordDto;
import nl.tudelft.sp.modelchecker.entities.PersistentCsv;
import nl.tudelft.sp.modelchecker.exceptions.ExistsException;

public interface PersistentCsvService extends CrudService<PersistentCsv, Long, CsvDto> {

    /**
     * Register a persistent csv.
     *
     * @param persistentCsv persistentCsv
     * @param containerDto  containerDto
     * @return registeredcsv
     * @throws ExistsException                          ExistsException
     * @throws NotFoundException                        NotFoundException
     * @throws SQLIntegrityConstraintViolationException SQLIntegrityConstraintViolationException
     */
    PersistentCsv register(PersistentCsv persistentCsv, ContainerDto containerDto)
            throws ExistsException, NotFoundException,
            SQLIntegrityConstraintViolationException;

    /**
     * Save a csv.
     *
     * @param containerDto containerDto
     * @param csvDto       csvDto
     * @return csv
     * @throws Exception Exception
     */
    PersistentCsv saveCsv(ContainerDto containerDto, CsvDto csvDto) throws Exception;

    /**
     * Get records in persistent csv.
     *
     * @param csvDto csvDto
     * @return records
     * @throws NotFoundException NotFoundException
     */
    List<RecordDto> getRecordsInPersistentCsv(CsvDto csvDto) throws NotFoundException;
}
