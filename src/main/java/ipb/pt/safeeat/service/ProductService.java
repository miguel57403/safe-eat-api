package ipb.pt.safeeat.service;

import ipb.pt.safeeat.utility.NotFoundConstants;
import ipb.pt.safeeat.dto.ProductDto;
import ipb.pt.safeeat.model.*;
import ipb.pt.safeeat.repository.CategoryRepository;
import ipb.pt.safeeat.repository.IngredientRepository;
import ipb.pt.safeeat.repository.ProductRepository;
import ipb.pt.safeeat.repository.RestaurantRepository;
import ipb.pt.safeeat.utility.RestrictionChecker;
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
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;

    public List<Product> findAll() {
        List<Product> products = productRepository.findAll();
        RestrictionChecker.checkProductList(products);
        return products;
    }

    public Product findById(String id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.PRODUCT_NOT_FOUND));

        RestrictionChecker.checkProduct(product);
        return product;
    }

    public List<Product> findAllByRestaurant(String id) {
        Restaurant restaurant = restaurantRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.RESTAURANT_NOT_FOUND));

        RestrictionChecker.checkProductList(restaurant.getProducts());
        return restaurant.getProducts();
    }

    public List<Product> findAllByRestaurantAndName(String id, String name) {
        Restaurant restaurant = restaurantRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.RESTAURANT_NOT_FOUND));

        List<Product> products = new ArrayList<>();
        for (Product product : restaurant.getProducts()) {
            if (product.getName().toLowerCase().contains(name.toLowerCase())) {
                products.add(product);
            }
        }

        RestrictionChecker.checkProductList(products);
        return products;
    }

    public Product create(ProductDto productDto, String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.RESTAURANT_NOT_FOUND));

        Category category = categoryRepository.findById(productDto.getCategoryId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.CATEGORY_NOT_FOUND));

        List<Ingredient> ingredients = new ArrayList<>();
        for (String ingredientId : productDto.getIngredientIds()) {
            ingredients.add(ingredientRepository.findById(ingredientId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.INGREDIENT_NOT_FOUND)));
        }

        Product product = new Product();
        BeanUtils.copyProperties(productDto, product);

        product.setIngredients(ingredients);
        product.setCategory(category);

        Product created = productRepository.save(product);

        restaurant.getProducts().add(created);
        restaurantRepository.save(restaurant);

        return created;
    }

    @Transactional
    public List<Product> createMany(List<ProductDto> productDtos, String restaurantId) {
        List<Product> created = new ArrayList<>();
        for (ProductDto productDto : productDtos) {
            created.add(create(productDto, restaurantId));
        }

        return created;
    }

    public Product update(ProductDto productDto) {
        Product old = productRepository.findById(productDto.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.PRODUCT_NOT_FOUND));

        BeanUtils.copyProperties(productDto, old);
        return productRepository.save(old);
    }

    public void delete(String id, String restaurantId) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.PRODUCT_NOT_FOUND));

        Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);

        if (restaurant.isPresent()) {
            restaurant.get().getProducts().remove(product);
            restaurantRepository.save(restaurant.get());
        }

        productRepository.deleteById(id);
    }
}
