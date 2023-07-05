package ipb.pt.safeeat.repository;

import ipb.pt.safeeat.model.Advertisement;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdvertisementRepository extends MongoRepository<Advertisement, String> {
    List<Advertisement> findAllByRestaurantId(String restaurantId);

    @Query(value = "{}, {$sample: {size: 3}}")
    List<Advertisement> findRandomAdvertisements();

    void deleteAllByRestaurantId(String restaurantId);
}
