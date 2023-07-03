package ipb.pt.safeeat.repository;

import ipb.pt.safeeat.model.Advertisement;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdvertisementRepository extends MongoRepository<Advertisement, String> {
    List<Advertisement> findAllByRestaurantId(String restaurantId);

    void deleteAllByRestaurantId(String restaurantId);
}
