package ipb.pt.safeeat.service;

import ipb.pt.safeeat.constant.IngredientConstants;
import ipb.pt.safeeat.constant.RestaurantConstants;
import ipb.pt.safeeat.constant.RestrictionConstants;
import ipb.pt.safeeat.dto.IngredientDto;
import ipb.pt.safeeat.model.Ingredient;
import ipb.pt.safeeat.model.Restaurant;
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

    public Ingredient create(IngredientDto ingredientDto, String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, RestaurantConstants.NOT_FOUND));

        if (ingredientDto.getRestrictionIds() != null && !ingredientDto.getRestrictionIds().isEmpty()) {
            for (String restrictionId : ingredientDto.getRestrictionIds()) {
                restrictionRepository.findById(restrictionId).orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, RestrictionConstants.NOT_FOUND));
            }
        }

        Ingredient ingredient = new Ingredient();
        BeanUtils.copyProperties(ingredientDto, ingredient);
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
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, IngredientConstants.NOT_FOUND));

        BeanUtils.copyProperties(ingredientDto, old);
        return ingredientRepository.save(old);
    }

    public void delete(String id) {
        ingredientRepository.deleteById(id);
    }
}
