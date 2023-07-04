package ipb.pt.safeeat.repository;

import ipb.pt.safeeat.model.Restriction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestrictionRepository extends MongoRepository<Restriction, String> {
    Optional<Restriction> findByName(String name);
}
