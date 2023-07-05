package ipb.pt.safeeat.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import ipb.pt.safeeat.config.AzureBlobConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.azure.storage.blob.BlobContainerClient;
import org.springframework.web.multipart.MultipartFile;
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

    public String uploadMultipartFile(MultipartFile imageFile, String previousImage, String folder, String name) throws IOException {
        InputStream imageStream = imageFile.getInputStream();
        String blobName = imageFile.getOriginalFilename();

        if (blobName == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image file is null");

        if (previousImage != null && !previousImage.isBlank()) {
            deleteRelativeBlob(previousImage);
        }

        String extension = blobName.substring(blobName.lastIndexOf(".") + 1);
        String partialBlobName = folder + "/" + name + "." + extension;
        uploadBlob(partialBlobName, imageStream);

        return getBlobUrl(partialBlobName);
    }

    public void deleteBlob(String blobName) {
        BlobClient blobClient = getBlobContainerClient().getBlobClient(blobName);
        blobClient.deleteIfExists();
    }

    public void deleteRelativeBlob(String relativeBlobName) {
        String containerUrl = getContainerUrl() + "/";
        deleteBlob(relativeBlobName.replace(containerUrl, ""));
    }

    public String getContainerUrl() {
        return getBlobContainerClient().getBlobContainerUrl();
    }
}
