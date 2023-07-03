package ipb.pt.safeeat.service;

import ipb.pt.safeeat.constant.NotFoundConstant;
import ipb.pt.safeeat.dto.CategoryDto;
import ipb.pt.safeeat.model.Category;
import ipb.pt.safeeat.repository.CategoryRepository;
import ipb.pt.safeeat.repository.ProductRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private AzureBlobService azureBlobService;

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category findById(String id) {
        return categoryRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.CATEGORY_NOT_FOUND));
    }

    public Category create(CategoryDto categoryDto) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDto, category);
        return categoryRepository.save(category);
    }

    public Category update(CategoryDto categoryDto) {
        Category old = categoryRepository.findById(categoryDto.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.CATEGORY_NOT_FOUND));

        BeanUtils.copyProperties(categoryDto, old);
        return categoryRepository.save(old);
    }

    public Category updateImage(String id, MultipartFile imageFile) throws IOException {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.CATEGORY_NOT_FOUND));

        InputStream imageStream = imageFile.getInputStream();
        String blobName = imageFile.getOriginalFilename();

        if (blobName == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image file is null");

        if (category.getImage() != null && !category.getImage().isBlank()) {
            String containerUrl = azureBlobService.getContainerUrl() + "/";
            azureBlobService.deleteBlob(category.getImage().replace(containerUrl, ""));
        }

        String extension = blobName.substring(blobName.lastIndexOf(".") + 1);
        String partialBlobName = "categories/" + category.getId() + "." + extension;
        azureBlobService.uploadBlob(partialBlobName, imageStream);

        String newBlobName = azureBlobService.getBlobUrl(partialBlobName);
        category.setImage(newBlobName);
        return categoryRepository.save(category);
    }

    public void delete(String id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.CATEGORY_NOT_FOUND));

        if (productRepository.findAllByCategory(category).size() > 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete category with products");

        if (category.getImage() != null && !category.getImage().isBlank()) {
            String containerUrl = azureBlobService.getContainerUrl() + "/";
            azureBlobService.deleteBlob(category.getImage().replace(containerUrl, ""));
        }

        categoryRepository.deleteById(id);
    }
}
