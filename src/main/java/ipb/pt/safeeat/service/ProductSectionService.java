package ipb.pt.safeeat.service;

import ipb.pt.safeeat.model.User;
import ipb.pt.safeeat.utility.NotFoundConstants;
import ipb.pt.safeeat.dto.ProductSectionDto;
import ipb.pt.safeeat.model.Product;
import ipb.pt.safeeat.model.ProductSection;
import ipb.pt.safeeat.model.Restaurant;
import ipb.pt.safeeat.repository.ProductRepository;
import ipb.pt.safeeat.repository.ProductSectionRepository;
import ipb.pt.safeeat.repository.RestaurantRepository;
import ipb.pt.safeeat.utility.RestrictionChecker;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
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
    @Autowired
    private RestrictionChecker restrictionChecker;

    public List<ProductSection> findAll() {
        List<ProductSection> productSections = productSectionRepository.findAll();

        for(ProductSection productSection : productSections) {
            restrictionChecker.checkProductList(productSection.getProducts());
        }

        return productSections;
    }

    public ProductSection findById(String id) {
        ProductSection productSection = productSectionRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.PRODUCT_SECTION_NOT_FOUND));

        restrictionChecker.checkProductList(productSection.getProducts());
        return productSection;
    }

    public List<ProductSection> findByRestaurant(String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.RESTAURANT_NOT_FOUND));

        List<ProductSection> productSections = restaurant.getProductSections();

        for(ProductSection productSection : productSections) {
            restrictionChecker.checkProductList(productSection.getProducts());
        }

        return productSections;
    }

    public ProductSection create(ProductSectionDto productSectionDto, String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found"));

        List<Product> products = new ArrayList<>();
        for (String productId : productSectionDto.getProductIds()) {
            products.add(productRepository.findById(productId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.PRODUCT_SECTION_NOT_FOUND)));
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
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.PRODUCT_SECTION_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Restaurant> restaurant = restaurantRepository.findByProductSection(old);

        if (restaurant.isEmpty() || !restaurant.get().getOwner().equals(user))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.RESTAURANT_NOT_FOUND);

        if (!restaurant.get().getProductSections().contains(old))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.PRODUCT_SECTION_NOT_FOUND);

        BeanUtils.copyProperties(productSectionDto, old);
        return productSectionRepository.save(old);
    }

    public void delete(String id) {
        ProductSection productSection = productSectionRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.PRODUCT_SECTION_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Restaurant> restaurant = restaurantRepository.findByProductSection(productSection);

        if (restaurant.isEmpty() || !restaurant.get().getOwner().equals(user))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.RESTAURANT_NOT_FOUND);

        restaurant.get().getProductSections().remove(productSection);
        restaurantRepository.save(restaurant.get());
        productSectionRepository.deleteById(id);
    }
}
