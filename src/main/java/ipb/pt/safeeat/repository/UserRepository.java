package ipb.pt.safeeat.repository;

import ipb.pt.safeeat.model.Order;
import ipb.pt.safeeat.model.Restriction;
import ipb.pt.safeeat.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);

    Optional<User> findByOrders(Order order);

    List<User> findByRestrictions(Restriction restriction);
}
