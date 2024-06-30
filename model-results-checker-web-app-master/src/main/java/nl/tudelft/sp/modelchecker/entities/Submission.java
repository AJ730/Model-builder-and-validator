package nl.tudelft.sp.modelchecker.entities;

import javax.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "submission")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Submission implements SuperEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private Container container;

    @OneToOne(fetch = FetchType.LAZY)
    private BasicUser client;


    /**
     * Constructor for Submission.
     *
     * @param container The container that is submitted
     * @param client    The client that submitted the container
     */
    public Submission(Container container, BasicUser client) {
        this.container = container;
        this.client = client;
    }

    /**
     * Remove all foreign key constraints.
     */
    @PreRemove
    public void remove() {
        if (container != null) {
            container.setSubmission(null);
        }
        if (client != null) {
            client.setSubmission(null);
        }
    }

}
