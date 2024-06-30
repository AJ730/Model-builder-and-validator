package nl.tudelft.sp.modelchecker.services.servicebeans;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.microsoft.azure.storage.blob.BlobContainerPermissions;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.SharedAccessBlobPermissions;
import com.microsoft.azure.storage.blob.SharedAccessBlobPolicy;
import java.time.OffsetDateTime;
import java.util.EnumSet;
import nl.tudelft.sp.modelchecker.cloud.Connection;
import nl.tudelft.sp.modelchecker.services.AzureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public abstract class AzureJpaService implements AzureService {

    private final BlobContainerPermissions permissions;
    @Autowired
    Environment environment;
    @Autowired
    Connection connection;
    @Autowired
    CloudBlobClient cloudBlobClient;
    private BlobServiceClient client;

    /**
     * empty constructor.
     */
    public AzureJpaService() {
        permissions = new BlobContainerPermissions();
    }

    /**
     * create permissions.
     *
     * @return BlobContainerPermissions
     */
    public BlobContainerPermissions createPermissions() {
        SharedAccessBlobPolicy readPolicy = new SharedAccessBlobPolicy();
        readPolicy.setPermissions(EnumSet.of(SharedAccessBlobPermissions.READ));
        permissions.getSharedAccessPolicies().put("DownloadPolicy", readPolicy);

        SharedAccessBlobPolicy writePolicy = new SharedAccessBlobPolicy();
        writePolicy.setPermissions(EnumSet.of(SharedAccessBlobPermissions.READ,
                SharedAccessBlobPermissions.WRITE, SharedAccessBlobPermissions.LIST,
                SharedAccessBlobPermissions.CREATE));
        permissions.getSharedAccessPolicies().put("UploadPolicy", writePolicy);

        return permissions;
    }

    /**
     * create sas token.
     *
     * @param blobClient blobClient
     * @return String
     */
    public String createSasToken(BlobClient blobClient) {
        BlobSasPermission blobSasPermission = new BlobSasPermission().setReadPermission(true);
        OffsetDateTime expiryTime = OffsetDateTime.now().plusMinutes(10);

        BlobServiceSasSignatureValues values = new BlobServiceSasSignatureValues(expiryTime,
                blobSasPermission).setStartTime(OffsetDateTime.now());

        return blobClient.generateSas(values);
    }

    /**
     * get BlobServiceClient.
     *
     * @return BlobServiceClient
     */
    public BlobServiceClient getClient() {
        initialize();
        return client;
    }

    private void initialize() {
        String connectionString = connection.getConnectionString();
        client = new BlobServiceClientBuilder()
            .connectionString(connectionString).buildClient();
    }
}
