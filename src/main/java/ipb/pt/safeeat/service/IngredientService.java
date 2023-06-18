package ipb.pt.safeeat.service;

import ipb.pt.safeeat.constants.IngredientConstants;
import ipb.pt.safeeat.constants.RestaurantConstants;
import ipb.pt.safeeat.constants.RestrictionConstants;
import ipb.pt.safeeat.model.Ingredient;
import ipb.pt.safeeat.model.Restaurant;
import ipb.pt.safeeat.model.Restriction;
import ipb.pt.safeeat.repository.IngredientRepository;
import ipb.pt.safeeat.repository.RestaurantRepository;
import ipb.pt.safeeat.repository.RestrictionRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    public List<Ingredient> getAll() {
        return ingredientRepository.findAll();
    }

    public Ingredient findById(String id) {
        return ingredientRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, IngredientConstants.NOT_FOUND));
    }

    public Ingredient create(Ingredient ingredient, String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, RestaurantConstants.NOT_FOUND));

        if (ingredient.getRestrictions() != null && !ingredient.getRestrictions().isEmpty()) {
            List<Restriction> restrictions = new ArrayList<>();
            for (Restriction restriction : ingredient.getRestrictions()) {
                restrictions.add(restrictionRepository.findById(restriction.getId()).orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, RestrictionConstants.NOT_FOUND)));
            }

            ingredient.setRestrictions(restrictions);
        }

        Ingredient created = ingredientRepository.save(ingredient);
        restaurant.getIngredients().add(created);
        restaurantRepository.save(restaurant);

        return created;
    }

    @Transactional
    public List<Ingredient> createMany(List<Ingredient> ingredients, String restaurantId) {
        List<Ingredient> created = new ArrayList<>();
        for(Ingredient ingredient : ingredients) {
            created.add(create(ingredient, restaurantId));
        }

        return created;
    }

    public Ingredient update(Ingredient ingredient) {
        Ingredient old = ingredientRepository.findById(ingredient.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, IngredientConstants.NOT_FOUND));

        BeanUtils.copyProperties(ingredient, old);
        return ingredientRepository.save(ingredient);
    }

    public void delete(String id) {
        ingredientRepository.deleteById(id);
    }
}
