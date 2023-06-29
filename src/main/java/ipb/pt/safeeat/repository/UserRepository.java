package ipb.pt.safeeat.repository;

import ipb.pt.safeeat.model.Address;
import ipb.pt.safeeat.model.Restriction;
import ipb.pt.safeeat.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByAddresses(Address address);
    List<User> findByRestrictions(Restriction restriction);
}
