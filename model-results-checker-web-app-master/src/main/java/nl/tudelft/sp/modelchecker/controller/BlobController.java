package nl.tudelft.sp.modelchecker.controller;


import com.microsoft.azure.storage.StorageException;
import java.net.URI;
import java.net.URISyntaxException;
import nl.tudelft.sp.modelchecker.cloud.Connection;
import nl.tudelft.sp.modelchecker.dto.BlobDto;
import nl.tudelft.sp.modelchecker.dto.BlobListDto;
import nl.tudelft.sp.modelchecker.dto.ContainerDto;
import nl.tudelft.sp.modelchecker.entities.Container;
import nl.tudelft.sp.modelchecker.services.AzureBlobService;
import nl.tudelft.sp.modelchecker.services.AzureContainerService;
import nl.tudelft.sp.modelchecker.services.ContainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/")
public class BlobController {

    @Autowired
    AzureBlobService azureBlobService;

    @Autowired
    ContainerService containerService;

    @Autowired
    AzureContainerService azureContainerService;

    @Autowired
    Connection connection;

    /**
     * get blob names in azure container.
     *
     * @return BlobListDto
     * @throws URISyntaxException URISyntaxException
     * @throws StorageException StorageException
     */
    @PostMapping("/get/blobList")
    @ResponseBody
    public ResponseEntity<BlobListDto> getBlobNames()
        throws URISyntaxException, StorageException {

        BlobListDto blobListDto = new BlobListDto();
        blobListDto.setBlobs(azureContainerService.listBlobs(connection.getDefaultContainer()));

        return new ResponseEntity<>(blobListDto, HttpStatus.OK);
    }


    /**
     * get the URI of the blob.
     *
     * @param containerDto containerDto
     * @return URI
     */
    @PostMapping("/get/blob")
    @ResponseBody
    public ResponseEntity<BlobDto> getBlobUri(@RequestBody ContainerDto containerDto) {

        Container container = containerService.findById(containerDto.getId());
        URI sasLink = azureBlobService.generateSasLink(connection.getDefaultContainer(),
            container.getBlobName());
        BlobDto blobDto = new BlobDto(sasLink);

        return new ResponseEntity<>(blobDto, HttpStatus.OK);
    }
}
