package ipb.pt.safeeat.repository;

import ipb.pt.safeeat.model.Feedback;
import ipb.pt.safeeat.model.Order;
import ipb.pt.safeeat.model.Restaurant;
import ipb.pt.safeeat.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    Optional<Order> findByFeedback(Feedback feedback);

    List<Order> findAllByClient(User client);

    List<Order> findAllByRestaurant(Restaurant restaurant);
}
