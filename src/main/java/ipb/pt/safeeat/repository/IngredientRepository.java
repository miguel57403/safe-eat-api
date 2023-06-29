package ipb.pt.safeeat.repository;

import ipb.pt.safeeat.model.Ingredient;
import ipb.pt.safeeat.model.Restriction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientRepository extends MongoRepository<Ingredient, String> {
    List<Ingredient> findByRestrictions(Restriction restriction);
}
