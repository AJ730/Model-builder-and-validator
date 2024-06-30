package nl.tudelft.sp.modelchecker.cloud;

import java.io.Serializable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class Connection implements Serializable {

    @Autowired
    Environment environment;

    /**
     * get connection string.
     * @return String
     */
    public String getConnectionString() {
        return environment.getProperty("azure.storage.ConnectionString");
    }

    /**
     * get cloud container name.
     *
     * @return String
     */
    public String getDefaultContainer() {
        return environment.getProperty("azure.storage.container.name");
    }
}
