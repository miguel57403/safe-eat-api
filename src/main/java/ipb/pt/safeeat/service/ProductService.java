package ipb.pt.safeeat.service;

import ipb.pt.safeeat.component.RestrictionChecker;
import ipb.pt.safeeat.constant.ForbiddenConstant;
import ipb.pt.safeeat.constant.NotFoundConstant;
import ipb.pt.safeeat.dto.ProductDto;
import ipb.pt.safeeat.model.*;
import ipb.pt.safeeat.repository.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private RestrictionChecker restrictionChecker;
    @Autowired
    private AzureBlobService azureBlobService;
    @Autowired
    private ItemRepository itemRepository;

    public List<Product> findAll() {
        List<Product> products = productRepository.findAll();
        restrictionChecker.checkProductList(products);
        return products;
    }

    public Product findById(String id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.PRODUCT_NOT_FOUND));

        restrictionChecker.checkProduct(product);
        return product;
    }

    public List<Product> findAllByRestaurant(String restaurantId) {
        List<Product> products = productRepository.findAllByRestaurantId(restaurantId);
        restrictionChecker.checkProductList(products);
        return products;
    }

    public List<Product> findAllByRestaurantAndName(String restaurantId, String name) {
        List<Product> products = productRepository.findAllByRestaurantIdAndName(restaurantId, name);
        restrictionChecker.checkProductList(products);
        return products;
    }

    public Product create(ProductDto productDto, String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        if (!restaurant.getOwnerId().equals(getAuthenticatedUser().getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_PRODUCT);

        List<Product> productEquals = productRepository.findAllByRestaurantIdAndName(restaurantId, productDto.getName());
        if (!productEquals.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ProductSection already exists");
        }

        categoryRepository.findById(productDto.getCategoryId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.CATEGORY_NOT_FOUND));

        for (String ingredientId : productDto.getIngredientIds()) {
            ingredientRepository.findById(ingredientId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.INGREDIENT_NOT_FOUND));
        }

        Product product = new Product();
        BeanUtils.copyProperties(productDto, product);
        product.setRestaurantId(restaurantId);

        Product created = productRepository.save(product);

        restrictionChecker.checkProduct(created);
        return created;
    }

    public Product update(ProductDto productDto) {
        Product old = productRepository.findById(productDto.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.PRODUCT_NOT_FOUND));

        Restaurant restaurant = restaurantRepository.findById(old.getRestaurantId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        if (!restaurant.getOwnerId().equals(getAuthenticatedUser().getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_PRODUCT);

        BeanUtils.copyProperties(productDto, old);
        Product updated = productRepository.save(old);

        restrictionChecker.checkProduct(updated);
        return updated;
    }

    public Product updateImage(String id, MultipartFile imageFile) throws IOException {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.CATEGORY_NOT_FOUND));

        InputStream imageStream = imageFile.getInputStream();
        String blobName = imageFile.getOriginalFilename();

        if (blobName == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image file is null");

        if (product.getImage() != null && !product.getImage().isBlank()) {
            String containerUrl = azureBlobService.getContainerUrl() + "/";
            azureBlobService.deleteBlob(product.getImage().replace(containerUrl, ""));
        }

        String extension = blobName.substring(blobName.lastIndexOf(".") + 1);
        String partialBlobName = "products/" + product.getId() + "." + extension;
        azureBlobService.uploadBlob(partialBlobName, imageStream);

        String newBlobName = azureBlobService.getBlobUrl(partialBlobName);
        product.setImage(newBlobName);
        Product updated = productRepository.save(product);

        restrictionChecker.checkProduct(updated);
        return updated;
    }

    public void delete(String id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.PRODUCT_NOT_FOUND));

        Restaurant restaurant = restaurantRepository.findById(product.getRestaurantId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        List<Item> items = itemRepository.findAllByProduct(product);

        if(!items.isEmpty())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ("The product cannot be deleted while someone is buying it"));

        if (!restaurant.getOwnerId().equals(getAuthenticatedUser().getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_PRODUCT);

        if (product.getImage() != null && !product.getImage().isBlank()) {
            String containerUrl = azureBlobService.getContainerUrl() + "/";
            String name = product.getImage().replace(containerUrl, "");
            azureBlobService.deleteBlob(name);
        }

        productRepository.deleteById(id);
    }

    private User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
