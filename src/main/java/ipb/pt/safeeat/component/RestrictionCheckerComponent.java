package ipb.pt.safeeat.component;

import ipb.pt.safeeat.model.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RestrictionCheckerComponent {
    public void checkProduct(Product product) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        product.setIsRestricted(false);
        for (Ingredient ingredient : product.getIngredients()) {
            for (Restriction restriction : ingredient.getRestrictions()) {
                if (user.getRestrictions().contains(restriction)) {
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
        for (Restriction restriction : ingredient.getRestrictions()) {
            ingredient.setIsRestricted(user.getRestrictions().contains(restriction));
        }
    }

    public void checkIngredientList(List<Ingredient> ingredients) {
        for (Ingredient ingredient : ingredients) {
            checkIngredient(ingredient);
        }
    }

    public void checkRestriction(Restriction restriction) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        restriction.setIsRestricted(user.getRestrictions().contains(restriction));
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
