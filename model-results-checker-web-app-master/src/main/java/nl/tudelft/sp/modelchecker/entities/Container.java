package nl.tudelft.sp.modelchecker.entities;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
@Table(name = "container")
@SuperBuilder
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Container extends GenericContainer {

    @ElementCollection
    private List<String> classes;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @EqualsAndHashCode.Exclude
    private Csv csv;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @EqualsAndHashCode.Exclude
    private PersistentCsv persistentCSv;

    @ManyToOne(fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    private Project project;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    private Submission submission;

    /**
     * Constructor for a container.
     *
     * @param frameRate   frameRate
     * @param description description
     * @param name        name
     */
    public Container(Double frameRate, String description, String name) {
        super(frameRate, description, name);
    }

    /**
     * Remove foreign key constraints.
     */
    @PreRemove
    public void remove() {
        if (submission != null) {
            submission.setContainer(null);
        }

        Set<Container> containers = new LinkedHashSet<>(project.getContainers()) {
        };
        containers.remove(this);
        project.setContainers(containers);
    }

}
