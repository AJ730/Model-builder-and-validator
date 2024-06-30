package nl.tudelft.sp.modelchecker.services;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import java.net.URISyntaxException;
import java.util.List;

public interface AzureContainerService extends AzureService {

    /**
     * get list of blob names in the cloud container.
     *
     * @param containerName containerName
     * @return List of String
     * @throws URISyntaxException URISyntaxException
     * @throws StorageException StorageException
     */
    List<String> listBlobs(String containerName)
            throws URISyntaxException, StorageException;

    /**
     * get CloudBlobContainer.
     *
     * @param containerName containerName
     * @return CloudBlobContainer
     * @throws URISyntaxException URISyntaxException
     * @throws StorageException StorageException
     */
    CloudBlobContainer getCloudBlobContainer(String containerName)
            throws URISyntaxException, StorageException;
}
