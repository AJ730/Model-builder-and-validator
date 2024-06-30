package nl.tudelft.sp.modelchecker.controller;

import java.util.List;
import javassist.NotFoundException;
import nl.tudelft.sp.modelchecker.dto.ContainerDto;
import nl.tudelft.sp.modelchecker.dto.CsvDto;
import nl.tudelft.sp.modelchecker.dto.RecordDto;
import nl.tudelft.sp.modelchecker.entities.Container;
import nl.tudelft.sp.modelchecker.entities.PersistentCsv;
import nl.tudelft.sp.modelchecker.services.ContainerService;
import nl.tudelft.sp.modelchecker.services.PersistentCsvService;
import nl.tudelft.sp.modelchecker.services.PersistentRecordService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
public class PersistentCsvController
        extends AbstractController<Long, PersistentCsvService, PersistentCsv, CsvDto> {

    @Autowired
    PersistentCsvService persistentCsvService;

    @Autowired
    PersistentRecordService persistentRecordService;

    @Autowired
    ContainerService containerService;


    /**
     * Instantiate Abstract Controller.
     *
     * @param service     service
     * @param modelMapper modelMapper
     */
    public PersistentCsvController(PersistentCsvService service,
                                   ModelMapper modelMapper) {
        super(service, modelMapper);
    }

    /**
     * Get records of a persistent csv.
     *
     * @param containerDto containerDto
     * @return records of a csv
     * @throws NotFoundException NotFoundException
     */
    @PostMapping("/records/persistentCsv")
    @ResponseBody
    public ResponseEntity<List<RecordDto>> getRecordDtosInPersistentCsv(
            @RequestBody ContainerDto containerDto)
            throws NotFoundException {

        Container container = containerService.findById(containerDto.getId());
        PersistentCsv persistentCsv = container.getPersistentCSv();
        List<RecordDto> recordDtos = service
                .getRecordsInPersistentCsv(new CsvDto(persistentCsv));
        return new ResponseEntity<>(recordDtos, HttpStatus.OK);
    }
}
