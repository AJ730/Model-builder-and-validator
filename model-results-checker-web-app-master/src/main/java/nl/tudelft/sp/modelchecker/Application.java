package nl.tudelft.sp.modelchecker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaRepositories
@EnableTransactionManagement
@EnableCaching
public class Application {

    /**
     * Instantiate the Server.
     *
     * @param args commandline arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

