package nl.tudelft.sp.modelchecker.config;


import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import nl.tudelft.sp.modelchecker.cloud.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureConfig {


    @Autowired
    private Connection connection;

    /**
     * create cloudBlobClient.
     *
     * @return cloudBlobClient
     * @throws URISyntaxException URISyntaxException
     * @throws InvalidKeyException InvalidKeyException
     */
    @Bean
    public CloudBlobClient cloudBlobClient() throws URISyntaxException, InvalidKeyException {
        CloudStorageAccount storageAccount = CloudStorageAccount
                .parse(connection.getConnectionString());
        return storageAccount.createCloudBlobClient();
    }
}
