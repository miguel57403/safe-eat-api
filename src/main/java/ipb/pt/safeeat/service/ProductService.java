package ipb.pt.safeeat.service;

import ipb.pt.safeeat.constant.CategoryConstants;
import ipb.pt.safeeat.constant.IngredientConstants;
import ipb.pt.safeeat.constant.ProductConstants;
import ipb.pt.safeeat.constant.RestaurantConstants;
import ipb.pt.safeeat.dto.ProductDto;
import ipb.pt.safeeat.model.Category;
import ipb.pt.safeeat.model.Ingredient;
import ipb.pt.safeeat.model.Product;
import ipb.pt.safeeat.model.Restaurant;
import ipb.pt.safeeat.repository.CategoryRepository;
import ipb.pt.safeeat.repository.IngredientRepository;
import ipb.pt.safeeat.repository.ProductRepository;
import ipb.pt.safeeat.repository.RestaurantRepository;
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

    public Product create(ProductDto productDto, String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, RestaurantConstants.NOT_FOUND));

        List<Ingredient> ingredients = new ArrayList<>();
        for (String ingredientId : productDto.getIngredientIds()) {
            ingredients.add(ingredientRepository.findById(ingredientId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, IngredientConstants.NOT_FOUND)));
        }

        List<Category> categories = new ArrayList<>();
        for (String categoryId : productDto.getCategoryIds()) {
            categories.add(categoryRepository.findById(categoryId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, CategoryConstants.NOT_FOUND)));
        }

        Product product = new Product();
        BeanUtils.copyProperties(productDto, product);

        product.setIngredients(ingredients);
        product.setCategories(categories);

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
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ProductConstants.NOT_FOUND));

        BeanUtils.copyProperties(productDto, old);
        return productRepository.save(old);
    }

    public void delete(String id) {
        productRepository.deleteById(id);
    }
}
