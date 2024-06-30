package nl.tudelft.sp.modelchecker.entities;

import javax.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@MappedSuperclass
@SuperBuilder
public abstract class GenericContainer implements SuperEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "container_id", unique = true, nullable = false)
    private Long id;

    @Column(name = "blob_name")
    private String blobName;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "frame_rate", nullable = false)
    private Double frameRate;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "csv_name")
    private String csvName;


    @Column(name = "class_name")
    private String className;

    /**
     * Generic Container constructor.
     *
     * @param frameRate   frameRate
     * @param description description
     * @param name        name
     */
    public GenericContainer(Double frameRate, String description, String name) {
        this.frameRate = frameRate;
        this.description = description;
        this.name = name;
    }

}
