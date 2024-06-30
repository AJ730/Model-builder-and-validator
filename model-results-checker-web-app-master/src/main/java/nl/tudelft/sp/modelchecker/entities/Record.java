package nl.tudelft.sp.modelchecker.entities;

import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "Record", indexes = {@Index(columnList = "csv_csv_id,objectId")})
@SuperBuilder
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Record extends GenericRecord {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    private Csv csv;

    /**
     * Remove foreign key constraints.
     */
    @PreRemove
    void remove() {
        Set<Record> records = new LinkedHashSet<>(csv.getRecords()) {
        };
        records.remove(this);
        csv.setRecords(records);
    }
}
