package nl.tudelft.sp.modelchecker.services;

import com.microsoft.azure.storage.StorageException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.transaction.NotSupportedException;

public interface AzureBlobService extends AzureService {


    /**
     * get fps of the blob.
     *
     * @param blobName blobName
     * @param containerName containerName
     * @return fps
     * @throws IOException IOException
     * @throws NotSupportedException NotSupportedException
     * @throws URISyntaxException URISyntaxException
     * @throws StorageException StorageException
     */
    double getFps(String blobName, String containerName) throws
            IOException, NotSupportedException, URISyntaxException, StorageException;

    /**
     * generate URI for the blob.
     *
     * @param containerName containerName
     * @param blobName blobName
     * @return URI
     */
    URI generateSasLink(String containerName, String blobName);
}
