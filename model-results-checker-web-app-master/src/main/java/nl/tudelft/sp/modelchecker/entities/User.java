package nl.tudelft.sp.modelchecker.entities;

import java.sql.Date;
import javax.persistence.*;
import javax.validation.constraints.Email;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor

@EqualsAndHashCode(callSuper = false)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(value = "type")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@ToString
@Table(name = "user_account")
public abstract class User implements SuperEntity<String> {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private String id;

    @Column(name = "email", nullable = false, unique = true)
    @Email
    private String email;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "registration_date", nullable = false)
    private Date registrationDate;


    /**
     * Constructor for User.
     *
     * @param email            Email of the user
     * @param username         Username of the user
     * @param registrationDate The time of registration of the user
     */
    public User(String id, String username, String email, Date registrationDate) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.registrationDate = registrationDate;
    }
}
