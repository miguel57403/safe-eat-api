package ipb.pt.safeeat.repository;

import ipb.pt.safeeat.model.ProductSection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSectionRepository extends MongoRepository<ProductSection, String> {
    List<ProductSection> findAllByRestaurantId(String restaurantId);

    List<ProductSection> findAllByRestaurantIdAndName(String restaurantId, String name);
}
