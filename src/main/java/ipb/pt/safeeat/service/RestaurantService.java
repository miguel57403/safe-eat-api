package ipb.pt.safeeat.service;

import ipb.pt.safeeat.constant.ForbiddenConstant;
import ipb.pt.safeeat.constant.NotFoundConstant;
import ipb.pt.safeeat.dto.RestaurantDto;
import ipb.pt.safeeat.model.Category;
import ipb.pt.safeeat.model.Product;
import ipb.pt.safeeat.model.Restaurant;
import ipb.pt.safeeat.model.User;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RestaurantService {
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductSectionRepository productSectionRepository;
    @Autowired
    private AdvertisementRepository advertisementRepository;
    @Autowired
    private DeliveryRepository deliveryRepository;
    @Autowired
    private AzureBlobService azureBlobService;

    public List<Restaurant> findAll() {
        return restaurantRepository.findAll();
    }

    public Restaurant findById(String id) {
        return restaurantRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));
    }

    // TODO: try to do this creating and calling a repository query
    public List<Restaurant> findAllByProductCategory(String categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.CATEGORY_NOT_FOUND));

        List<Product> products = productRepository.findAllByCategory(category);
        Set<String> restaurantIds = products.stream()
                .map(Product::getRestaurantId)
                .collect(Collectors.toSet());

        return restaurantRepository.findAllById(restaurantIds);
    }

    public List<Restaurant> findAllByOwner(String ownerId) {
        User owner = userRepository.findById(ownerId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.USER_NOT_FOUND));

        return restaurantRepository.findAllByOwner(owner);
    }

    public Restaurant findByCart() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!user.getCart().getItems().isEmpty()) {
            return restaurantRepository.findById(user.getCart().getItems().get(0).getProduct().getRestaurantId()).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));
        }

        return null;
    }

    public List<Restaurant> findAllByName(String name) {
        return restaurantRepository.findAllByName(name);
    }

    public Restaurant create(RestaurantDto restaurantDto) {
        User owner = getAuthenticatedUser();
        Restaurant restaurant = new Restaurant();
        BeanUtils.copyProperties(restaurantDto, restaurant);
        restaurant.setOwner(owner);

        return restaurantRepository.save(restaurant);
    }

    public Restaurant update(RestaurantDto restaurantDto) {
        Restaurant old = restaurantRepository.findById(restaurantDto.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        if (!old.getOwner().getId().equals(getAuthenticatedUser().getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_RESTAURANT);

        BeanUtils.copyProperties(restaurantDto, old);
        return restaurantRepository.save(old);
    }

    public Restaurant updateLogo(String id, MultipartFile imageFile) throws IOException {
        Restaurant restaurant = restaurantRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.CATEGORY_NOT_FOUND));

        if (!restaurant.getOwner().equals(getAuthenticatedUser()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_RESTAURANT);

        InputStream imageStream = imageFile.getInputStream();
        String blobName = imageFile.getOriginalFilename();

        if (blobName == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image file is null");

        if (restaurant.getLogo() != null && !restaurant.getLogo().isBlank()) {
            String containerUrl = azureBlobService.getContainerUrl() + "/";
            azureBlobService.deleteBlob(restaurant.getLogo().replace(containerUrl, ""));
        }

        String extension = blobName.substring(blobName.lastIndexOf(".") + 1);
        String partialBlobName = "restaurants/logos/" + restaurant.getId() + "." + extension;
        azureBlobService.uploadBlob(partialBlobName, imageStream);

        String newBlobName = azureBlobService.getBlobUrl(partialBlobName);
        restaurant.setLogo(newBlobName);
        return restaurantRepository.save(restaurant);
    }

    public Restaurant updateCover(String id, MultipartFile imageFile) throws IOException {
        Restaurant restaurant = restaurantRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.CATEGORY_NOT_FOUND));

        if (!restaurant.getOwner().equals(getAuthenticatedUser()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_RESTAURANT);

        InputStream imageStream = imageFile.getInputStream();
        String blobName = imageFile.getOriginalFilename();

        if (blobName == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image file is null");

        if (restaurant.getCover() != null && !restaurant.getCover().isBlank()) {
            String containerUrl = azureBlobService.getContainerUrl() + "/";
            azureBlobService.deleteBlob(restaurant.getCover().replace(containerUrl, ""));
        }

        String extension = blobName.substring(blobName.lastIndexOf(".") + 1);
        String partialBlobName = "restaurants/covers/" + restaurant.getId() + "." + extension;
        azureBlobService.uploadBlob(partialBlobName, imageStream);

        String newBlobName = azureBlobService.getBlobUrl(partialBlobName);
        restaurant.setCover(newBlobName);
        return restaurantRepository.save(restaurant);
    }

    public void delete(String id) {
        Restaurant restaurant = restaurantRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        if (!restaurant.getOwner().equals(getAuthenticatedUser()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_RESTAURANT);

        if (restaurant.getLogo() != null && !restaurant.getLogo().isBlank()) {
            String containerUrl = azureBlobService.getContainerUrl() + "/";
            azureBlobService.deleteBlob(restaurant.getLogo().replace(containerUrl, ""));
        }

        if (restaurant.getCover() != null && !restaurant.getCover().isBlank()) {
            String containerUrl = azureBlobService.getContainerUrl() + "/";
            azureBlobService.deleteBlob(restaurant.getCover().replace(containerUrl, ""));
        }

        productRepository.deleteAllByRestaurantId(restaurant.getId());
        advertisementRepository.deleteAllByRestaurantId(restaurant.getId());

        ingredientRepository.deleteAll(restaurant.getIngredients());
        productSectionRepository.deleteAll(restaurant.getProductSections());
        deliveryRepository.deleteAll(restaurant.getDeliveries());

        restaurantRepository.deleteById(id);
    }

    private User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
