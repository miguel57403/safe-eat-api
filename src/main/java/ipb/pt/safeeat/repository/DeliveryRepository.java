package ipb.pt.safeeat.repository;

import ipb.pt.safeeat.model.Delivery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryRepository extends MongoRepository<Delivery, String> {
}
