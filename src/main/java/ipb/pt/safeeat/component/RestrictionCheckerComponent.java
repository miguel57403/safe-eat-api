package ipb.pt.safeeat.component;

import ipb.pt.safeeat.model.*;
import ipb.pt.safeeat.repository.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RestrictionCheckerComponent {
    @Autowired
    private IngredientRepository ingredientRepository;

    public void checkProduct(Product product) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Ingredient> productIngredients = ingredientRepository.findAllById(product.getIngredientIds());

        product.setIsRestricted(false);
        for (Ingredient ingredient : productIngredients) {
            for (String restrictionId : ingredient.getRestrictionIds()) {
                if (user.getRestrictionIds().contains(restrictionId)) {
                    product.setIsRestricted(true);
                    break;
                }
            }
        }
    }

    public void checkProductList(List<Product> products) {
        for (Product product : products) {
            checkProduct(product);
        }
    }

    public void checkIngredient(Ingredient ingredient) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ingredient.setIsRestricted(false);
        for (String restrictionId : ingredient.getRestrictionIds()) {
            ingredient.setIsRestricted(user.getRestrictionIds().contains(restrictionId));
        }
    }

    public void checkIngredientList(List<Ingredient> ingredients) {
        for (Ingredient ingredient : ingredients) {
            checkIngredient(ingredient);
        }
    }

    public void checkRestriction(Restriction restriction) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        restriction.setIsRestricted(user.getRestrictionIds().contains(restriction.getId()));
    }

    public void checkRestrictionList(List<Restriction> restrictions) {
        for (Restriction restriction : restrictions) {
            checkRestriction(restriction);
        }
    }

    public void checkItem(Item item) {
        checkProduct(item.getProduct());
    }

    public void checkItemList(List<Item> items) {
        for (Item item : items) {
            checkItem(item);
        }
    }
}
