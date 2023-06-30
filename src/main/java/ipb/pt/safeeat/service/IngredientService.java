package ipb.pt.safeeat.service;

import ipb.pt.safeeat.component.RestrictionCheckerComponent;
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

import java.util.ArrayList;
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
    private RestrictionCheckerComponent restrictionCheckerComponent;

    public List<Ingredient> findAll() {
        List<Ingredient> ingredients = ingredientRepository.findAll();
        restrictionCheckerComponent.checkIngredientList(ingredients);

        return ingredients;
    }

    public Ingredient findById(String id) {
        Ingredient ingredient = ingredientRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.INGREDIENT_NOT_FOUND));

        restrictionCheckerComponent.checkIngredient(ingredient);

        return ingredient;
    }

    public List<Ingredient> findAllByRestaurant(String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!user.isAdmin() && !restaurant.getOwner().equals(user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_INGREDIENT);

        List<Ingredient> ingredients = restaurant.getIngredients();
        restrictionCheckerComponent.checkIngredientList(ingredients);

        return ingredients;
    }

    public List<Ingredient> findAllByProduct(String productId) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.PRODUCT_NOT_FOUND));

        List<Ingredient> ingredients = product.getIngredients();
        restrictionCheckerComponent.checkIngredientList(ingredients);

        return ingredients;
    }

    public Ingredient create(IngredientDto ingredientDto, String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!restaurant.getOwner().equals(user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_INGREDIENT);

        List<Restriction> restrictions = new ArrayList<>();
        if (ingredientDto.getRestrictionIds() != null && !ingredientDto.getRestrictionIds().isEmpty()) {
            for (String restrictionId : ingredientDto.getRestrictionIds()) {
                restrictions.add(restrictionRepository.findById(restrictionId).orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTRICTION_NOT_FOUND)));
            }
        }

        Ingredient ingredient = new Ingredient();
        BeanUtils.copyProperties(ingredientDto, ingredient);
        ingredient.setRestrictions(restrictions);

        Ingredient created = ingredientRepository.save(ingredient);

        restaurant.getIngredients().add(created);
        restaurantRepository.save(restaurant);

        return created;
    }

    public Ingredient update(IngredientDto ingredientDto) {
        Ingredient old = ingredientRepository.findById(ingredientDto.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.INGREDIENT_NOT_FOUND));

        Restaurant restaurant = restaurantRepository.findByIngredients(old).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!restaurant.getOwner().equals(user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_INGREDIENT);

        if (!restaurant.getIngredients().contains(old))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_INGREDIENT);

        BeanUtils.copyProperties(ingredientDto, old);
        return ingredientRepository.save(old);
    }

    public void delete(String id) {
        Ingredient ingredient = ingredientRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.INGREDIENT_NOT_FOUND));

        Restaurant restaurant = restaurantRepository.findByIngredients(ingredient).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!restaurant.getOwner().equals(user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_INGREDIENT);

        if (!restaurant.getIngredients().contains(ingredient))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_INGREDIENT);

        restaurant.getIngredients().remove(ingredient);
        restaurantRepository.save(restaurant);

        for (Product product : restaurant.getProducts()) {
            if (product.getIngredients().contains(ingredient)) {
                product.getIngredients().remove(ingredient);
                productRepository.save(product);
            }
        }

        ingredientRepository.deleteById(id);
    }
}
