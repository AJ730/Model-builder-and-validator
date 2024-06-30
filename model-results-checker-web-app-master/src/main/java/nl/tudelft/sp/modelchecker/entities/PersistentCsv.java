package nl.tudelft.sp.modelchecker.entities;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "persistent_csv")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PersistentCsv implements SuperEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "persistent_csv_id", unique = true, nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private Container container;
    @OneToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.ALL},
            mappedBy = "persistentCsv")
    @Fetch(FetchMode.JOIN)
    private Set<PersistentRecord> persistentRecords = new HashSet<>();

    /**
     * Remove foreignKey Constraints.
     */
    @PreRemove
    public void remove() {
        if (container != null) {
            container.setPersistentCSv(null);
        }
    }
}
