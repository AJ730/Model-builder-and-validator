package nl.tudelft.sp.modelchecker.entities;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Table(name = "projectHolder")
public class ProjectHolder implements SuperEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "projectHolder_id", unique = true, nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    private BasicUser client;

    @OneToMany(mappedBy = "projectHolder", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private Set<Project> projects = new HashSet<>();

    /**
     * Remove foreign key constraints.
     */
    @PreRemove
    void remove() {
        if (client != null) {
            client.setProjectHolder(null);
        }
    }
}
