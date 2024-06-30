package nl.tudelft.sp.modelchecker.controller;


import java.io.IOException;
import java.util.List;
import javassist.NotFoundException;
import nl.tudelft.sp.modelchecker.dto.*;
import nl.tudelft.sp.modelchecker.entities.Container;
import nl.tudelft.sp.modelchecker.entities.Csv;
import nl.tudelft.sp.modelchecker.exceptions.ExistsException;
import nl.tudelft.sp.modelchecker.services.ContainerService;
import nl.tudelft.sp.modelchecker.services.CsvService;
import nl.tudelft.sp.modelchecker.services.servicebeans.RecordJpaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/")
public class CsvController extends AbstractController<Long, CsvService, Csv, CsvDto> {

    @Autowired
    RecordJpaService recordJpaService;

    @Autowired
    ContainerService containerService;

    @Autowired
    CsvService csvService;

    /**
     * Initialize a csv controller.
     *
     * @param service     service
     * @param modelMapper modelMapper
     */
    public CsvController(CsvService service, ModelMapper modelMapper) {
        super(service, modelMapper);
    }

    /**
     * Get a csv from a csvDto.
     *
     * @param csvDto cSvDto
     * @return csv
     * @throws NotFoundException NotFoundException
     */
    @PostMapping("/get/csv")
    @ResponseBody
    public ResponseEntity<CsvDto> get(@RequestBody CsvDto csvDto)
            throws NotFoundException {
        return super.getSpecific(csvDto);
    }

    /**
     * save csv file.
     *
     * @param recordListDto recordListDto.
     * @return saved csv
     * @throws NotFoundException NotFoundException
     * @throws ExistsException   ExistsException
     * @throws IOException       IOException
     */
    @PostMapping("/save/csv")
    @ResponseBody
    public ResponseEntity<Void> save(@RequestBody RecordListDto recordListDto)
            throws NotFoundException, ExistsException, IOException {
        containerService.overideContainer(recordListDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Delete a csv.
     *
     * @param csvDto cSvDto
     * @return deleted csv
     * @throws NotFoundException NotFoundException
     */
    @PostMapping("/delete/csv")
    @ResponseBody
    @PreAuthorize("hasAuthority('APPROLE_ADMIN')")
    public ResponseEntity<CsvDto> delete(@RequestBody CsvDto csvDto)
            throws NotFoundException {
        return super.delete(csvDto);
    }

    /**
     * delete records in csv file.
     *
     * @param deleteDto deleteDto
     * @return Response entity
     */
    @PostMapping("/delete/records")
    @ResponseBody
    public ResponseEntity<Void> deleteRecords(@RequestBody DeleteDto<RecordDto> deleteDto) {
        recordJpaService.delete(deleteDto.getRecordDtos());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * List all csvs.
     *
     * @return list
     * @throws NotFoundException NotFoundException
     */
    @PostMapping("/list/csv")
    @ResponseBody
    @PreAuthorize("hasAuthority('APPROLE_ADMIN')")
    public ResponseEntity<List<CsvDto>> getCsvs() throws NotFoundException {
        return super.list(CsvDto.class);
    }

    /**
     * Get records of a csv.
     *
     * @param containerDto containerDto
     * @return records of a csv
     * @throws NotFoundException NotFoundException
     */
    @PostMapping("/records/csv")
    @ResponseBody
    public ResponseEntity<List<RecordDto>> getRecordDtosInCsv(
            @RequestBody ContainerDto containerDto)
            throws NotFoundException {

        Container container = containerService.findById(containerDto.getId());
        Csv csv = container.getCsv();
        List<RecordDto> recordDtos = service.getRecordsInCsv(new CsvDto(csv));
        return new ResponseEntity<>(recordDtos, HttpStatus.OK);
    }
}
