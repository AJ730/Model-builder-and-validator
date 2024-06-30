package nl.tudelft.sp.modelchecker.entities;

import javax.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import nl.tudelft.sp.modelchecker.dto.RecordDto;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@MappedSuperclass
@SuperBuilder
public abstract class GenericRecord implements SuperEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "record_id", unique = true, nullable = false)
    private Long id;

    private int frameNum;

    private int objectId;

    private String label;

    private int trackerL;

    private int trackerT;

    private int trackerW;

    private int trackerH;

    private double modelConfidence;

    private double trackerConfidence;

    /**
     * Update attributes of a recordDto.
     *
     * @param newDto newDto
     */
    public void updateAttributes(RecordDto newDto) {
        setFrameNum(newDto.getFrameNum());
        setObjectId(newDto.getObjectId());
        setLabel(newDto.getLabel());
        setTrackerL(newDto.getTrackerL());
        setTrackerT(newDto.getTrackerT());
        setTrackerW(newDto.getTrackerW());
        setTrackerH(newDto.getTrackerH());
        setModelConfidence(newDto.getModelConfidence());
        setTrackerConfidence(newDto.getTrackerConfidence());
    }

}
