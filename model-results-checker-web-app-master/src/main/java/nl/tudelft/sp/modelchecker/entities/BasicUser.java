package nl.tudelft.sp.modelchecker.entities;

import java.sql.Date;
import javax.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@DiscriminatorValue("BasicUser")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class BasicUser extends User {

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Submission submission;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private ProjectHolder projectHolder;

    /**
     * Constructor for basicUser.
     *
     * @param username         username
     * @param email            email
     * @param registrationTime registrationTime
     */
    public BasicUser(String id, String username, String email, Date registrationTime) {
        super(id, username, email, registrationTime);
    }
}
