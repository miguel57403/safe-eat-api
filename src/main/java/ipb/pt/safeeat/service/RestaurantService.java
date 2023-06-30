package ipb.pt.safeeat.service;

import ipb.pt.safeeat.constant.ForbiddenConstant;
import ipb.pt.safeeat.constant.NotFoundConstant;
import ipb.pt.safeeat.dto.RestaurantDto;
import ipb.pt.safeeat.model.Category;
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
import java.util.ArrayList;
import java.util.List;

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

    public List<Restaurant> findAllByProductCategory(String categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.CATEGORY_NOT_FOUND));

        return restaurantRepository.findAllByProductsCategory(category);
    }

    public List<Restaurant> findAllByOwner(String ownerId) {
        User owner = userRepository.findById(ownerId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.USER_NOT_FOUND));

        List<Restaurant> restaurants = new ArrayList<>();
        for (Restaurant restaurant : owner.getRestaurants()) {
            restaurants.add(restaurantRepository.findById(restaurant.getId()).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND)));
        }

        return restaurants;
    }

    public List<Restaurant> findAllByName(String name) {
        List<Restaurant> restaurants = new ArrayList<>();
        for (Restaurant restaurant : restaurantRepository.findAll()) {
            if (restaurant.getName().toLowerCase().contains(name.toLowerCase())) {
                restaurants.add(restaurant);
            }
        }

        return restaurants;
    }

    public Restaurant create(RestaurantDto restaurantDto) {
        User owner = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!restaurantDto.getOwnerId().equals(owner.getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_RESTAURANT);

        Restaurant restaurant = new Restaurant();
        BeanUtils.copyProperties(restaurantDto, restaurant);

        Restaurant created = restaurantRepository.save(restaurant);

        owner.getRestaurants().add(created);
        userRepository.save(owner);

        return created;
    }

    public Restaurant update(RestaurantDto restaurantDto) {
        Restaurant old = restaurantRepository.findById(restaurantDto.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!old.getOwner().getId().equals(user.getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_RESTAURANT);

        BeanUtils.copyProperties(restaurantDto, old);
        return restaurantRepository.save(old);
    }

    public Restaurant updateLogo(String id, MultipartFile imageFile) throws IOException {
        Restaurant restaurant = restaurantRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.CATEGORY_NOT_FOUND));

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

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (restaurant.getOwner() != null && !restaurant.getOwner().getId().equals(user.getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_RESTAURANT);

        if (restaurant.getLogo() != null && !restaurant.getLogo().isBlank()) {
            String containerUrl = azureBlobService.getContainerUrl() + "/";
            azureBlobService.deleteBlob(restaurant.getLogo().replace(containerUrl, ""));
        }

        if (restaurant.getCover() != null && !restaurant.getCover().isBlank()) {
            String containerUrl = azureBlobService.getContainerUrl() + "/";
            azureBlobService.deleteBlob(restaurant.getCover().replace(containerUrl, ""));
        }

        productRepository.deleteAll(restaurant.getProducts());
        ingredientRepository.deleteAll(restaurant.getIngredients());
        productSectionRepository.deleteAll(restaurant.getProductSections());
        advertisementRepository.deleteAll(restaurant.getAdvertisements());
        deliveryRepository.deleteAll(restaurant.getDeliveries());

        user.getRestaurants().remove(restaurant);
        userRepository.save(user);

        restaurantRepository.deleteById(id);
    }
}
