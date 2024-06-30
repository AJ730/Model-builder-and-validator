package nl.tudelft.sp.modelchecker.azurebeans;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.microsoft.azure.storage.blob.BlobContainerPermissions;
import com.microsoft.azure.storage.blob.SharedAccessBlobPermissions;
import com.microsoft.azure.storage.blob.SharedAccessBlobPolicy;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.EnumSet;
import nl.tudelft.sp.modelchecker.Application;
import nl.tudelft.sp.modelchecker.services.servicebeans.AzureBlobJpaService;
import nl.tudelft.sp.modelchecker.services.servicebeans.AzureContainerJpaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = Application.class)
@Transactional
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class AzureContainerJpaServiceTest {

    @Autowired
    AzureBlobJpaService azureBlobJpaService;

    /**
     * test create permission.
     *
     */
    @Test
    void createPermissionTest() {
        AzureContainerJpaService service =
            new AzureContainerJpaService();

        BlobContainerPermissions permissions = service.createPermissions();
        SharedAccessBlobPolicy read = permissions
            .getSharedAccessPolicies()
            .get("DownloadPolicy");

        SharedAccessBlobPolicy write = permissions
            .getSharedAccessPolicies()
            .get("UploadPolicy");

        assertThat(read.getPermissions())
            .isEqualTo(EnumSet.of(SharedAccessBlobPermissions.READ));

        assertThat(write.getPermissions())
            .isEqualTo(EnumSet.of(SharedAccessBlobPermissions.READ,
                SharedAccessBlobPermissions.WRITE, SharedAccessBlobPermissions.LIST,
                SharedAccessBlobPermissions.CREATE));
    }

    @Test
    void smokeLinkTest() throws IOException {
        URL link = new URL(azureBlobJpaService
                .generateSasLink("videos", "output1.mp4").toString());
        HttpURLConnection huc = (HttpURLConnection) link.openConnection();
        huc.setRequestMethod("HEAD");

        int responseCode = huc.getResponseCode();
        assertEquals(HTTP_OK, responseCode);
    }

}