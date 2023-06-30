package ipb.pt.safeeat.controller;

import ipb.pt.safeeat.service.AzureBlobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Controller
@CrossOrigin
@RequestMapping("/")
public class HelloController {
    @Autowired
    private AzureBlobService azureBlobService;

    @GetMapping
    public ResponseEntity<Object> hello() {
        return ResponseEntity.ok("Hello SafeEat!");
    }

    @GetMapping("/image/{name}")
    public ResponseEntity<Object> getImage(@PathVariable String name) {
        return ResponseEntity.ok(azureBlobService.getBlobUrl(name));
    }

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public void uploadImage(@RequestParam("image") MultipartFile imageFile) throws IOException {
        InputStream imageStream = imageFile.getInputStream();
        String blobName = imageFile.getOriginalFilename();
        azureBlobService.uploadBlob(blobName, imageStream);
    }
}
