package ipb.pt.safeeat.repository;

import ipb.pt.safeeat.model.Advertisement;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdvertisementRepository extends MongoRepository<Advertisement, String> {
}
