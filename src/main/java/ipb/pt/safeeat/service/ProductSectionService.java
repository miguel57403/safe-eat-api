package ipb.pt.safeeat.service;

import ipb.pt.safeeat.constants.ProductSectionConstants;
import ipb.pt.safeeat.constants.RestaurantConstants;
import ipb.pt.safeeat.model.Product;
import ipb.pt.safeeat.model.ProductSection;
import ipb.pt.safeeat.model.Restaurant;
import ipb.pt.safeeat.repository.ProductRepository;
import ipb.pt.safeeat.repository.ProductSectionRepository;
import ipb.pt.safeeat.repository.RestaurantRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductSectionService {
    @Autowired
    private ProductSectionRepository productSectionRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;

    public List<ProductSection> getAll() {
        return productSectionRepository.findAll();
    }

    public ProductSection findById(String id) {
        return productSectionRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ProductSectionConstants.NOT_FOUND));
    }

    public ProductSection create(ProductSection productSection, String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, RestaurantConstants.NOT_FOUND));

        List<Product> products = new ArrayList<>();
        for(Product product : productSection.getProducts()) {
            Product original = productRepository.findById(product.getId()).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ProductSectionConstants.NOT_FOUND));

            products.add(original);
        }

        productSection.setProducts(products);
        ProductSection created = productSectionRepository.save(productSection);

        restaurant.getProductSections().add(created);
        restaurantRepository.save(restaurant);

        return created;
    }

    @Transactional
    public List<ProductSection> createMany(List<ProductSection> productSections, String restaurantId) {
        List<ProductSection> created = new ArrayList<>();
        for(ProductSection productSection : productSections) {
            created.add(create(productSection, restaurantId));
        }

        return created;
    }

    public ProductSection update(ProductSection productSection) {
        ProductSection old = productSectionRepository.findById(productSection.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ProductSectionConstants.NOT_FOUND));

        BeanUtils.copyProperties(productSection, old);
        return productSectionRepository.save(productSection);
    }

    public void delete(String id) {
        productSectionRepository.deleteById(id);
    }
}
