package ipb.pt.safeeat.repository;

import ipb.pt.safeeat.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {
}
