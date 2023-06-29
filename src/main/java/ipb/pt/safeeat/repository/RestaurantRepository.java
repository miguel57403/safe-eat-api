package ipb.pt.safeeat.repository;

import ipb.pt.safeeat.model.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestaurantRepository extends MongoRepository<Restaurant, String> {
    Optional<Restaurant> findByAdvertisements(Advertisement advertisement);
    Optional<Restaurant> findByDeliveries(Delivery delivery);
    Optional<Restaurant> findByIngredients(Ingredient ingredient);
    Optional<Restaurant> findByProductSection(ProductSection productSection);
    Optional<Restaurant> findByProducts(Product product);
}
