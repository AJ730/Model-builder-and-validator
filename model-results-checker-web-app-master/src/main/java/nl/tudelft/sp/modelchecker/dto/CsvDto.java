package nl.tudelft.sp.modelchecker.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.tudelft.sp.modelchecker.entities.Csv;
import nl.tudelft.sp.modelchecker.entities.PersistentCsv;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CsvDto extends Dto<Long> {

    private Long containerId;


    /**
     * Constructor for CsvDto.
     *
     * @param id id
     */
    public CsvDto(long id) {
        super(id);
    }

    /**
     * Constructor for CsvDto.
     *
     * @param csv csv
     */
    public CsvDto(Csv csv) {
        super(csv.getId());
        if (csv.getContainer() != null) {
            this.containerId = csv.getContainer().getId();
        }
    }

    /**
     * Constructor for PersistentCsvDto.
     *
     * @param persistentCsv persistentCsv
     */
    public CsvDto(PersistentCsv persistentCsv) {
        super(persistentCsv.getId());
        if (persistentCsv.getContainer() != null) {
            this.containerId = persistentCsv.getContainer().getId();
        }
    }

}
