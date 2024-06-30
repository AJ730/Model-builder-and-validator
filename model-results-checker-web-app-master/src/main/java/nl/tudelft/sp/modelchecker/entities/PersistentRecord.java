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
@Table(name = "PersistentRecord")
@SuperBuilder
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PersistentRecord extends GenericRecord {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    private PersistentCsv persistentCsv;


    @PreRemove
    void remove() {
        Set<PersistentRecord> persistentRecords = new LinkedHashSet<>(
                persistentCsv.getPersistentRecords()) {
        };
        persistentRecords.remove(this);
        persistentCsv.setPersistentRecords(persistentRecords);
    }
}
