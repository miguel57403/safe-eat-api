package ipb.pt.safeeat.service;

import ipb.pt.safeeat.constants.ExceptionConstants;
import ipb.pt.safeeat.dto.RestaurantDto;
import ipb.pt.safeeat.model.Category;
import ipb.pt.safeeat.model.Product;
import ipb.pt.safeeat.model.Restaurant;
import ipb.pt.safeeat.model.User;
import ipb.pt.safeeat.repository.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public List<Restaurant> getAll() {
        try {
            return restaurantRepository.findAll();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public Restaurant findById(String id) {
        return restaurantRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ExceptionConstants.RESTAURANT_NOT_FOUND));
    }

    public List<Restaurant> findByProductCategory(String categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ExceptionConstants.CATEGORY_NOT_FOUND));

        List<Restaurant> restaurants = new ArrayList<>();
        for (Restaurant restaurant : restaurantRepository.findAll()) {
            for (Product product : restaurant.getProducts()) {
                if (product.getCategory().getId().equals(category.getId())) {
                    restaurants.add(restaurant);
                    break;
                }
            }
        }

        return restaurants;
    }

    public List<Restaurant> findByOwner(String ownerId) {
        User owner = userRepository.findById(ownerId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ExceptionConstants.USER_NOT_FOUND));

        List<Restaurant> restaurants = new ArrayList<>();
        for (Restaurant restaurant : owner.getRestaurants()) {
            restaurants.add(restaurantRepository.findById(restaurant.getId()).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ExceptionConstants.RESTAURANT_NOT_FOUND)));
        }

        return restaurants;
    }

    public Restaurant create(RestaurantDto restaurantDto) {
        User owner = userRepository.findById(restaurantDto.getOwnerId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ExceptionConstants.USER_NOT_FOUND));

        Restaurant restaurant = new Restaurant();
        BeanUtils.copyProperties(restaurantDto, restaurant);

        Restaurant created = restaurantRepository.save(restaurant);

        owner.getRestaurants().add(created);
        userRepository.save(owner);

        return created;
    }

    @Transactional
    public List<Restaurant> createMany(List<RestaurantDto> restaurantDtos) {
        List<Restaurant> created = new ArrayList<>();
        for (RestaurantDto restaurantDto : restaurantDtos) {
            created.add(create(restaurantDto));
        }

        return created;
    }

    public Restaurant update(RestaurantDto restaurantDto) {
        Restaurant old = restaurantRepository.findById(restaurantDto.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ExceptionConstants.RESTAURANT_NOT_FOUND));

        BeanUtils.copyProperties(restaurantDto, old);
        return restaurantRepository.save(old);
    }

    public void delete(String id) {
        Restaurant restaurant = restaurantRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ExceptionConstants.RESTAURANT_NOT_FOUND));

        Optional<User> owner = userRepository.findById(restaurant.getOwner().getId());

        productRepository.deleteAll(restaurant.getProducts());
        ingredientRepository.deleteAll(restaurant.getIngredients());
        productSectionRepository.deleteAll(restaurant.getProductSections());
        advertisementRepository.deleteAll(restaurant.getAdvertisements());
        deliveryRepository.deleteAll(restaurant.getDeliveries());

        if (owner.isPresent()) {
            owner.get().getRestaurants().remove(restaurant);
            userRepository.save(owner.get());
        }

        restaurantRepository.deleteById(id);
    }
}
