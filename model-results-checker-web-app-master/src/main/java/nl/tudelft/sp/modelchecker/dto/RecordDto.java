package nl.tudelft.sp.modelchecker.dto;


import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RecordDto extends Dto<Long> implements Comparable<RecordDto> {

    private int frameNum;

    private int objectId;

    private String label;

    private int trackerL;

    private int trackerT;

    private int trackerW;

    private int trackerH;

    private double modelConfidence;

    private double trackerConfidence;

    private Long csvId;

    /**
     * Constructor for RecordDto.
     *
     * @param id id
     */
    public RecordDto(long id) {
        super(id);
    }

    /**
     * Compare to another Record.
     *
     * @param o o
     * @return record
     */
    @Override
    public int compareTo(RecordDto o) {
        return this.id.compareTo(o.id);
    }

}
