package ipb.pt.safeeat.utility;

import ipb.pt.safeeat.model.Ingredient;
import ipb.pt.safeeat.model.Product;
import ipb.pt.safeeat.model.Restriction;
import ipb.pt.safeeat.model.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RestrictionChecker {
    public void checkProduct(Product product) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        for (Ingredient ingredient : product.getIngredients()) {
            for (Restriction restriction : ingredient.getRestrictions()) {
                if (user.getRestrictions().contains(restriction)) {
                    product.setIsRestricted(true);
                }
            }
        }
    }

    public void checkProductList(List<Product> products) {
        for(Product product : products) {
            checkProduct(product);
        }
    }

    public void checkIngredient(Ingredient ingredient) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        for (Restriction restriction : ingredient.getRestrictions()) {
            if (user.getRestrictions().contains(restriction)) {
                ingredient.setIsRestricted(true);
            }
        }
    }

    public void checkIngredientList(List<Ingredient> ingredients) {
        for(Ingredient ingredient : ingredients) {
            checkIngredient(ingredient);
        }
    }

    public void checkRestriction(Restriction restriction) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user.getRestrictions().contains(restriction)) {
            restriction.setIsRestricted(true);
        }
    }

    public void checkRestrictionList(List<Restriction> restrictions) {
        for(Restriction restriction : restrictions) {
            checkRestriction(restriction);
        }
    }
}
