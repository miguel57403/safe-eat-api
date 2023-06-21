package ipb.pt.safeeat.repository;

import ipb.pt.safeeat.model.Category;
import ipb.pt.safeeat.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findAllByCategory(Category category);
}
