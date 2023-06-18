package ipb.pt.safeeat.service;

import ipb.pt.safeeat.constants.*;
import ipb.pt.safeeat.model.*;
import ipb.pt.safeeat.repository.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
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

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public Product findById(String id) {
        return productRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ProductConstants.NOT_FOUND));
    }

    public Product create(Product product, String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, RestaurantConstants.NOT_FOUND));

        List<Ingredient> ingredients = new ArrayList<>();
        for(Ingredient ingredient : product.getIngredients()) {
            ingredients.add(ingredientRepository.findById(ingredient.getId()).orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, IngredientConstants.NOT_FOUND)));
        }

        List<Category> categories = new ArrayList<>();
        for(Category category : product.getCategories()) {
            categories.add(categoryRepository.findById(category.getId()).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, CategoryConstants.NOT_FOUND)));
        }

        product.setIngredients(ingredients);
        product.setCategories(categories);

        Product created = productRepository.save(product);

        restaurant.getProducts().add(created);
        restaurantRepository.save(restaurant);

        return created;
    }

    @Transactional
    public List<Product> createMany(List<Product> products, String restaurantId) {
        List<Product> created = new ArrayList<>();
        for(Product product : products) {
            created.add(create(product, restaurantId));
        }

        return created;
    }

    public Product update(Product product) {
        Product old = productRepository.findById(product.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ProductConstants.NOT_FOUND));

        BeanUtils.copyProperties(product, old);
        return productRepository.save(product);
    }

    public void delete(String id) {
        productRepository.deleteById(id);
    }
}
