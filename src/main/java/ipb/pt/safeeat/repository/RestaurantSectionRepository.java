package ipb.pt.safeeat.repository;

import ipb.pt.safeeat.model.RestaurantSection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantSectionRepository extends MongoRepository<RestaurantSection, String> {
    @Query(value = "{}, {$sample: {size: 8}}")
    List<RestaurantSection> findRandomRestaurantSections();
}
