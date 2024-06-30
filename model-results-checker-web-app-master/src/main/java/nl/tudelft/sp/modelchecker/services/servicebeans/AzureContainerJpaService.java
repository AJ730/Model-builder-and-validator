package nl.tudelft.sp.modelchecker.services.servicebeans;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.ListBlobItem;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sp.modelchecker.services.AzureContainerService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AzureContainerJpaService extends AzureJpaService implements AzureContainerService {

    @Autowired
    CloudBlobClient cloudBlobClient;


    /**
     * get list of blob names in the cloud container.
     *
     * @param containerName containerName
     * @return List of String
     * @throws URISyntaxException URISyntaxException
     * @throws StorageException StorageException
     */
    public List<String> listBlobs(String containerName)
            throws URISyntaxException, StorageException {

        CloudBlobContainer container = cloudBlobClient.getContainerReference(containerName);
        Iterable<ListBlobItem> items = container.listBlobs();
        List<String> blobs = new ArrayList<>();
        for (ListBlobItem x : items) {
            blobs.add(FilenameUtils.getName(x.getUri().getPath()));
        }
        return blobs;
    }

    /**
     * get CloudBlobContainer.
     *
     * @param containerName containerName
     * @return CloudBlobContainer
     * @throws URISyntaxException URISyntaxException
     * @throws StorageException StorageException
     */
    public CloudBlobContainer getCloudBlobContainer(String containerName)
            throws URISyntaxException, StorageException {
        return cloudBlobClient.getContainerReference(containerName);
    }
}
