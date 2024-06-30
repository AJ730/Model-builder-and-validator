package nl.tudelft.sp.modelchecker.services;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobServiceClient;
import com.microsoft.azure.storage.blob.BlobContainerPermissions;


public interface AzureService {

    /**
     * create permissions.
     *
     * @return BlobContainerPermissions
     */
    BlobContainerPermissions createPermissions();

    /**
     * create sas token.
     *
     * @param blobClient blobClient
     * @return String
     */
    String createSasToken(BlobClient blobClient);

    /**
     * get BlobServiceClient.
     *
     * @return BlobServiceClient
     */
    BlobServiceClient getClient();
}
