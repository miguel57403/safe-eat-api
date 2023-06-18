package ipb.pt.safeeat.repository;

import ipb.pt.safeeat.model.RestaurantSection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RestaurantSectionRepository extends MongoRepository<RestaurantSection, String> {
}
