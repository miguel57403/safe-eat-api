package ipb.pt.safeeat.service;

import ipb.pt.safeeat.dto.IngredientDto;
import ipb.pt.safeeat.model.*;
import ipb.pt.safeeat.repository.IngredientRepository;
import ipb.pt.safeeat.repository.ProductRepository;
import ipb.pt.safeeat.repository.RestaurantRepository;
import ipb.pt.safeeat.repository.RestrictionRepository;
import ipb.pt.safeeat.utility.NotFoundConstants;
import ipb.pt.safeeat.utility.RestrictionChecker;
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
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.INGREDIENT_NOT_FOUND));

        restrictionChecker.checkRestrictionList(ingredient.getRestrictions());
        return ingredient;
    }

    public Ingredient create(IngredientDto ingredientDto, String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.RESTAURANT_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!restaurant.getOwner().equals(user))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.RESTAURANT_NOT_FOUND);

        List<Restriction> restrictions = new ArrayList<>();
        if (ingredientDto.getRestrictionIds() != null && !ingredientDto.getRestrictionIds().isEmpty()) {
            for (String restrictionId : ingredientDto.getRestrictionIds()) {
                restrictions.add(restrictionRepository.findById(restrictionId).orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.RESTRICTION_NOT_FOUND)));
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

    @Transactional
    public List<Ingredient> createMany(List<IngredientDto> ingredientDtos, String restaurantId) {
        List<Ingredient> created = new ArrayList<>();
        for (IngredientDto ingredientDto : ingredientDtos) {
            created.add(create(ingredientDto, restaurantId));
        }

        return created;
    }

    public Ingredient update(IngredientDto ingredientDto) {
        Ingredient old = ingredientRepository.findById(ingredientDto.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.INGREDIENT_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Restaurant restaurant = restaurantRepository.findByIngredients(old).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.RESTAURANT_NOT_FOUND));

        if (!restaurant.getOwner().equals(user))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.RESTAURANT_NOT_FOUND);

        if (!restaurant.getIngredients().contains(old))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.INGREDIENT_NOT_FOUND);

        BeanUtils.copyProperties(ingredientDto, old);
        return ingredientRepository.save(old);
    }

    public void delete(String id) {
        Ingredient ingredient = ingredientRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.INGREDIENT_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Restaurant restaurant = restaurantRepository.findByIngredients(ingredient).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.RESTAURANT_NOT_FOUND));

        if (!restaurant.getOwner().equals(user))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.RESTAURANT_NOT_FOUND);

        if (!restaurant.getIngredients().contains(ingredient))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.INGREDIENT_NOT_FOUND);

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
