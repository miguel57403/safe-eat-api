package ipb.pt.safeeat.repository;

import ipb.pt.safeeat.model.Feedback;
import ipb.pt.safeeat.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    Optional<Order> findByFeedback(Feedback feedback);
}
