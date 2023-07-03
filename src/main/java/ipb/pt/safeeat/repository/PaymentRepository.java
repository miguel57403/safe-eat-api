package ipb.pt.safeeat.repository;

import ipb.pt.safeeat.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {
    List<Payment> findAllByUserId(String userId);
    void deleteAllByUserId(String userId);
}
