package ipb.pt.safeeat.repository;

import ipb.pt.safeeat.model.Address;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends MongoRepository<Address, String> {
    List<Address> findAllByUserId(String userId);
    void deleteAllByUserId(String userId);
}
