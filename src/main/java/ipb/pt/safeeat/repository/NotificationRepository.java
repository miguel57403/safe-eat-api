package ipb.pt.safeeat.repository;

import ipb.pt.safeeat.model.Notification;
import ipb.pt.safeeat.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findAllByOrder(Order order);
}
