package nl.tudelft.sp.modelchecker.services.servicebeans;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobServiceClient;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import javax.transaction.NotSupportedException;
import nl.tudelft.sp.modelchecker.cloud.Connection;
import nl.tudelft.sp.modelchecker.services.AzureBlobService;
import nl.tudelft.sp.modelchecker.videoprocessing.VideoProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AzureBlobJpaService extends AzureJpaService implements AzureBlobService {

    @Autowired
    AzureContainerJpaService azureContainerJpaService;

    @Autowired
    Connection connection;

    /**
     * get fps of the blob in the cloud container.
     *
     * @param blobName blobName
     * @param containerName containerName
     * @return fps
     * @throws IOException IOException
     * @throws NotSupportedException NotSupportedException
     * @throws URISyntaxException URISyntaxException
     * @throws StorageException StorageException
     */
    public double getFps(String blobName, String containerName) throws
            IOException, NotSupportedException, URISyntaxException, StorageException {
        VideoProcessor videoProcessor = new VideoProcessor();
        InputStream inputStream = fetchBlob(blobName, containerName);
        videoProcessor.run(inputStream);

        return videoProcessor.getFps();
    }

    /**
     * generate the SAS link of the blob.
     *
     * @param containerName containerName
     * @param blobName blobName
     * @return URI
     */
    public URI generateSasLink(String containerName, String blobName) {

        BlobServiceClient client = getClient();

        BlobClient blobClient = client
            .getBlobContainerClient(containerName).getBlobClient(blobName);

        return URI.create(blobClient.getBlobUrl() + "?" + createSasToken(blobClient));
    }

    private InputStream fetchBlob(String blobName, String containerName)
            throws URISyntaxException, StorageException {
        CloudBlobContainer cloudBlobContainer =
            azureContainerJpaService.getCloudBlobContainer(containerName);
        CloudBlockBlob cloudBlockBlob = cloudBlobContainer.getBlockBlobReference(blobName);
        return cloudBlockBlob.openInputStream();
    }
}
