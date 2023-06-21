package ipb.pt.safeeat.service;

import ipb.pt.safeeat.constants.ExceptionConstants;
import ipb.pt.safeeat.dto.ProductSectionDto;
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
import java.util.Optional;

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
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ExceptionConstants.PRODUCT_SECTION_NOT_FOUND));
    }

    public ProductSection create(ProductSectionDto productSectionDto, String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ExceptionConstants.RESTAURANT_NOT_FOUND));

        List<Product> products = new ArrayList<>();
        for (String productId : productSectionDto.getProductIds()) {
            products.add(productRepository.findById(productId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ExceptionConstants.PRODUCT_SECTION_NOT_FOUND)));
        }

        ProductSection productSection = new ProductSection();
        BeanUtils.copyProperties(productSectionDto, productSection);

        productSection.setProducts(products);
        ProductSection created = productSectionRepository.save(productSection);

        restaurant.getProductSections().add(created);
        restaurantRepository.save(restaurant);

        return created;
    }

    @Transactional
    public List<ProductSection> createMany(List<ProductSectionDto> productSectionDtos, String restaurantId) {
        List<ProductSection> created = new ArrayList<>();
        for (ProductSectionDto productSectionDto : productSectionDtos) {
            created.add(create(productSectionDto, restaurantId));
        }

        return created;
    }

    public ProductSection update(ProductSectionDto productSectionDto) {
        ProductSection old = productSectionRepository.findById(productSectionDto.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ExceptionConstants.PRODUCT_SECTION_NOT_FOUND));

        BeanUtils.copyProperties(productSectionDto, old);
        return productSectionRepository.save(old);
    }

    public void delete(String id, String restaurantId) {
        ProductSection productSection = productSectionRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ExceptionConstants.PRODUCT_SECTION_NOT_FOUND));

        Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);

        if (restaurant.isPresent()) {
            restaurant.get().getProductSections().remove(productSection);
            restaurantRepository.save(restaurant.get());
        }

        productSectionRepository.deleteById(id);
    }
}
