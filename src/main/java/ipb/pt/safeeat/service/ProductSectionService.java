package ipb.pt.safeeat.service;

import ipb.pt.safeeat.component.RestrictionCheckerComponent;
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
    private RestrictionCheckerComponent restrictionCheckerComponent;

    public List<ProductSection> findAll() {
        List<ProductSection> productSections = productSectionRepository.findAll();

        for (ProductSection productSection : productSections) {
            restrictionCheckerComponent.checkProductList(productSection.getProducts());
        }

        return productSections;
    }

    public ProductSection findById(String id) {
        return productSectionRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.PRODUCT_SECTION_NOT_FOUND));
    }

    public List<ProductSection> findAllByRestaurant(String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        List<ProductSection> productSections = restaurant.getProductSections();

        for (ProductSection productSection : productSections) {
            restrictionCheckerComponent.checkProductList(productSection.getProducts());
        }

        return productSections;
    }

    public ProductSection create(ProductSectionDto productSectionDto, String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!restaurant.getOwner().getId().equals(user.getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_RESTAURANT);

        List<Product> products = new ArrayList<>();
        for (String productId : productSectionDto.getProductIds()) {
            products.add(productRepository.findById(productId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.PRODUCT_SECTION_NOT_FOUND)));
        }

        ProductSection productSection = new ProductSection();
        BeanUtils.copyProperties(productSectionDto, productSection);

        productSection.setProducts(products);
        ProductSection created = productSectionRepository.save(productSection);

        restaurant.getProductSections().add(created);
        restaurantRepository.save(restaurant);

        return created;
    }

    public ProductSection update(ProductSectionDto productSectionDto) {
        ProductSection old = productSectionRepository.findById(productSectionDto.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.PRODUCT_SECTION_NOT_FOUND));

        Restaurant restaurant = restaurantRepository.findByProductSections(old).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!restaurant.getOwner().equals(user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_PRODUCT_SECTION);

        if (!restaurant.getProductSections().contains(old))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_PRODUCT_SECTION);

        BeanUtils.copyProperties(productSectionDto, old);
        return productSectionRepository.save(old);
    }

    public void delete(String id) {
        ProductSection productSection = productSectionRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.PRODUCT_SECTION_NOT_FOUND));

        Restaurant restaurant = restaurantRepository.findByProductSections(productSection).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!restaurant.getOwner().equals(user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_PRODUCT_SECTION);

        restaurant.getProductSections().remove(productSection);
        restaurantRepository.save(restaurant);

        productSectionRepository.deleteById(id);
    }
}
