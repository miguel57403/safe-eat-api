package ipb.pt.safeeat.repository;

import ipb.pt.safeeat.model.Item;
import ipb.pt.safeeat.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends MongoRepository<Item, String> {
    List<Item> findAllByProduct(Product product);
}
