package nl.tudelft.sp.modelchecker.repositories;

import nl.tudelft.sp.modelchecker.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository<U extends User> extends JpaRepository<U, String> {

    /**
     * Find using username.
     *
     * @param username username
     * @return Type U
     */
    U findByUsername(String username);

    /**
     * Find using emails.
     *
     * @param email email
     * @return Type U
     */
    U findByEmail(String email);
}
