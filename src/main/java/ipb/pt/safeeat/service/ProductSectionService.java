package ipb.pt.safeeat.service;

import ipb.pt.safeeat.component.RestrictionChecker;
import ipb.pt.safeeat.constant.ForbiddenConstant;
import ipb.pt.safeeat.constant.NotFoundConstant;
import ipb.pt.safeeat.dto.ProductSectionDto;
import ipb.pt.safeeat.model.Product;
import ipb.pt.safeeat.model.ProductSection;
import ipb.pt.safeeat.model.Restaurant;
import ipb.pt.safeeat.model.User;
import ipb.pt.safeeat.repository.ProductRepository;
import ipb.pt.safeeat.repository.ProductSectionRepository;
import ipb.pt.safeeat.repository.RestaurantRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
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
    @Autowired
    private RestrictionChecker restrictionChecker;

    public List<ProductSection> findAll() {
        List<ProductSection> productSections = productSectionRepository.findAll();

        for (ProductSection productSection : productSections) {
            restrictionChecker.checkProductList(productSection.getProducts());
        }

        return productSections;
    }

    public ProductSection findById(String id) {
        ProductSection productSection = productSectionRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.PRODUCT_SECTION_NOT_FOUND));

        restrictionChecker.checkProductList(productSection.getProducts());
        return productSection;
    }

    public List<ProductSection> findAllByRestaurant(String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        List<ProductSection> productSections = productSectionRepository.findAllByRestaurantId(restaurant.getId());

        for (ProductSection productSection : productSections) {
            restrictionChecker.checkProductList(productSection.getProducts());
        }

        return productSections;
    }

    public ProductSection create(ProductSectionDto productSectionDto, String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        if (!restaurant.getOwnerId().equals(getAuthenticatedUser().getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_RESTAURANT);

        List<ProductSection> productSectionsEquals = productSectionRepository.findAllByRestaurantIdAndName(restaurantId, productSectionDto.getName());
        if (!productSectionsEquals.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ProductSection already exists");
        }

        List<Product> products = new ArrayList<>();
        for (String productId : productSectionDto.getProductIds()) {
            products.add(productRepository.findById(productId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.PRODUCT_SECTION_NOT_FOUND)));
        }

        for(Product product : products) {
            if(!productRepository.findAllByRestaurantId(restaurantId).contains(product))
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_PRODUCT_SECTION);
        }

        ProductSection productSection = new ProductSection();
        BeanUtils.copyProperties(productSectionDto, productSection);

        productSection.setProducts(products);
        productSection.setRestaurantId(restaurantId);
        ProductSection created = productSectionRepository.save(productSection);

        restrictionChecker.checkProductList(created.getProducts());
        return created;
    }

    public ProductSection update(ProductSectionDto productSectionDto) {
        ProductSection old = productSectionRepository.findById(productSectionDto.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.PRODUCT_SECTION_NOT_FOUND));

        Restaurant restaurant = restaurantRepository.findById(old.getRestaurantId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        if (!restaurant.getOwnerId().equals(getAuthenticatedUser().getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_PRODUCT_SECTION);

        if (!restaurant.getId().equals(old.getRestaurantId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_PRODUCT_SECTION);

        BeanUtils.copyProperties(productSectionDto, old);
        ProductSection updated = productSectionRepository.save(old);

        restrictionChecker.checkProductList(updated.getProducts());
        return updated;
    }

    public void delete(String id) {
        ProductSection productSection = productSectionRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.PRODUCT_SECTION_NOT_FOUND));

        Restaurant restaurant = restaurantRepository.findById(productSection.getRestaurantId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        if (!restaurant.getOwnerId().equals(getAuthenticatedUser().getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_PRODUCT_SECTION);

        productSectionRepository.deleteById(id);
    }

    private User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
