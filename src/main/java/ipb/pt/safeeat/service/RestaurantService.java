package ipb.pt.safeeat.service;

import ipb.pt.safeeat.dto.RestaurantDto;
import ipb.pt.safeeat.model.Category;
import ipb.pt.safeeat.model.Product;
import ipb.pt.safeeat.model.Restaurant;
import ipb.pt.safeeat.model.User;
import ipb.pt.safeeat.repository.*;
import ipb.pt.safeeat.utility.ForbiddenConstants;
import ipb.pt.safeeat.utility.NotFoundConstants;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

    public List<Restaurant> findAll() {
        return restaurantRepository.findAll();
    }

    public Restaurant findById(String id) {
        return restaurantRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.RESTAURANT_NOT_FOUND));
    }

    public List<Restaurant> findByProductCategory(String categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.CATEGORY_NOT_FOUND));

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

    public List<Restaurant> findAllByOwner(String ownerId) {
        User owner = userRepository.findById(ownerId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.USER_NOT_FOUND));

        List<Restaurant> restaurants = new ArrayList<>();
        for (Restaurant restaurant : owner.getRestaurants()) {
            restaurants.add(restaurantRepository.findById(restaurant.getId()).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.RESTAURANT_NOT_FOUND)));
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
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstants.FORBIDDEN_RESTAURANT);

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
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.RESTAURANT_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!old.getOwner().getId().equals(user.getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstants.FORBIDDEN_RESTAURANT);

        BeanUtils.copyProperties(restaurantDto, old);
        return restaurantRepository.save(old);
    }

    public void delete(String id) {
        Restaurant restaurant = restaurantRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.RESTAURANT_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (restaurant.getOwner() != null && !restaurant.getOwner().getId().equals(user.getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstants.FORBIDDEN_RESTAURANT);

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
