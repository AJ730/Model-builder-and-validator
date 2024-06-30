package nl.tudelft.sp.modelchecker.entities;


import java.sql.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@DiscriminatorValue("Admin")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Admin extends User {


    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            mappedBy = "admin")
    @Fetch(FetchMode.SELECT)
    private Set<Project> projects = new HashSet<>();

    /**
     * Constructor for admin.
     *
     * @param username         username
     * @param email            email
     * @param registrationTime registrationTime
     */
    public Admin(String id, String username, String email, Date registrationTime) {
        super(id, username, email, registrationTime);
    }

}
