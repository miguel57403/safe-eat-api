package ipb.pt.safeeat.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import ipb.pt.safeeat.config.AzureBlobConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.azure.storage.blob.BlobContainerClient;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class AzureBlobService {
    private final AzureBlobConfig azureBlobConfig;

    private BlobContainerClient getBlobContainerClient() {
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(azureBlobConfig.getUrl())
                .buildClient();
        return blobServiceClient.getBlobContainerClient(azureBlobConfig.getContainer());
    }

    public String getBlobUrl(String blobName) {
        return getBlobContainerClient().getBlobClient(blobName).getBlobUrl();
    }

    public void uploadBlob(String blobName, InputStream inputStream) {
        try {
            BlobClient blobClient = getBlobContainerClient().getBlobClient(blobName);
            blobClient.upload(inputStream, inputStream.available(), true);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected input stream error");
        }
    }

    public void deleteBlob(String blobName) {
        BlobClient blobClient = getBlobContainerClient().getBlobClient(blobName);
        blobClient.deleteIfExists();
    }

    public String getContainerUrl() {
        return getBlobContainerClient().getBlobContainerUrl();
    }
}
