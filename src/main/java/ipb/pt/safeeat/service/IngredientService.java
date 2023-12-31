package ipb.pt.safeeat.service;

import ipb.pt.safeeat.component.RestrictionChecker;
import ipb.pt.safeeat.constant.ForbiddenConstant;
import ipb.pt.safeeat.constant.NotFoundConstant;
import ipb.pt.safeeat.dto.IngredientDto;
import ipb.pt.safeeat.model.*;
import ipb.pt.safeeat.repository.IngredientRepository;
import ipb.pt.safeeat.repository.ProductRepository;
import ipb.pt.safeeat.repository.RestaurantRepository;
import ipb.pt.safeeat.repository.RestrictionRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class IngredientService {
    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private RestrictionRepository restrictionRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private RestrictionChecker restrictionChecker;

    public List<Ingredient> findAll() {
        List<Ingredient> ingredients = ingredientRepository.findAll();
        restrictionChecker.checkIngredientList(ingredients);

        return ingredients;
    }

    public Ingredient findById(String id) {
        Ingredient ingredient = ingredientRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.INGREDIENT_NOT_FOUND));

        restrictionChecker.checkIngredient(ingredient);

        return ingredient;
    }

    public List<Ingredient> findAllByRestaurant(String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        if (!getAuthenticatedUser().isAdmin() && !restaurant.getOwnerId().equals(getAuthenticatedUser().getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_INGREDIENT);

        List<Ingredient> ingredients = ingredientRepository.findAllByRestaurantId(restaurantId);
        restrictionChecker.checkIngredientList(ingredients);

        return ingredients;
    }

    public List<Ingredient> findAllByProduct(String productId) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.PRODUCT_NOT_FOUND));

        List<Ingredient> ingredients = ingredientRepository.findAllById(product.getIngredientIds());
        restrictionChecker.checkIngredientList(ingredients);

        return ingredients;
    }

    public Ingredient create(IngredientDto ingredientDto, String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        if (!restaurant.getOwnerId().equals(getAuthenticatedUser().getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_INGREDIENT);

        List<Ingredient> ingredientsEquals = ingredientRepository.findAllByRestaurantIdAndName(restaurantId, ingredientDto.getName());
        if (!ingredientsEquals.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ingredient already exists");
        }

        for (String restrictionId : ingredientDto.getRestrictionIds()) {
            restrictionRepository.findById(restrictionId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTRICTION_NOT_FOUND));
        }

        Ingredient ingredient = new Ingredient();
        ingredient.setRestaurantId(restaurantId);
        BeanUtils.copyProperties(ingredientDto, ingredient);

        Ingredient created = ingredientRepository.save(ingredient);

        restaurantRepository.save(restaurant);
        restrictionChecker.checkIngredient(ingredient);

        return created;
    }

    public Ingredient update(IngredientDto ingredientDto) {
        Ingredient old = ingredientRepository.findById(ingredientDto.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.INGREDIENT_NOT_FOUND));

        Restaurant restaurant = restaurantRepository.findById(old.getRestaurantId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        for (String restrictionId : ingredientDto.getRestrictionIds()) {
            restrictionRepository.findById(restrictionId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTRICTION_NOT_FOUND));
        }

        if (!restaurant.getOwnerId().equals(getAuthenticatedUser().getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_INGREDIENT);

        if (!restaurant.getId().equals(old.getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_INGREDIENT);

        BeanUtils.copyProperties(ingredientDto, old);
        Ingredient updated = ingredientRepository.save(old);
        restrictionChecker.checkIngredient(updated);

        return updated;
    }

    public void delete(String id) {
        Ingredient ingredient = ingredientRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.INGREDIENT_NOT_FOUND));

        Restaurant restaurant = restaurantRepository.findById(ingredient.getRestaurantId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        if (!restaurant.getOwnerId().equals(getAuthenticatedUser().getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_INGREDIENT);

        if (!restaurant.getId().equals(ingredient.getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_INGREDIENT);

        List<Product> products = productRepository.findAllByRestaurantId(restaurant.getId());

        for (Product product : products) {
            if (product.getIngredientIds().contains(ingredient.getId())) {
                product.getIngredientIds().remove(ingredient.getId());
                productRepository.save(product);
            }
        }

        ingredientRepository.deleteById(id);
    }

    private User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
