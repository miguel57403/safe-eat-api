package ipb.pt.safeeat.repository;

import ipb.pt.safeeat.model.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends MongoRepository<Restaurant, String> {
    //TODO: rever isto
    Optional<Restaurant> findAllByDeliveries(Delivery delivery);

    List<Restaurant> findAllByOwnerId(String ownerId);

    List<Restaurant> findAllByName(String name);

    List<Restaurant> findByNameContainingIgnoreCase(String name);
}
