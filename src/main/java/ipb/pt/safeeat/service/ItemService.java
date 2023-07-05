package ipb.pt.safeeat.service;

import ipb.pt.safeeat.component.RestrictionChecker;
import ipb.pt.safeeat.constant.ForbiddenConstant;
import ipb.pt.safeeat.constant.NotFoundConstant;
import ipb.pt.safeeat.dto.ItemDto;
import ipb.pt.safeeat.model.*;
import ipb.pt.safeeat.repository.CartRepository;
import ipb.pt.safeeat.repository.ItemRepository;
import ipb.pt.safeeat.repository.ProductRepository;
import ipb.pt.safeeat.repository.RestaurantRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private RestrictionChecker restrictionChecker;

    public List<Item> findAll() {
        List<Item> items = itemRepository.findAll();
        restrictionChecker.checkItemList(items);
        return items;
    }

    public Item findById(String id) {
        Item item = itemRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.ITEM_NOT_FOUND));

        User user = getAuthenticatedUser();
        Cart cart = cartRepository.findById(user.getCartId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.CART_NOT_FOUND));

        if (!user.isAdmin() && !cart.getItems().contains(item))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_ITEM);

        restrictionChecker.checkItem(item);
        return item;
    }

    public List<Item> findAllByCart(String cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.CART_NOT_FOUND));

        if (!getAuthenticatedUser().isAdmin() && !getAuthenticatedUser().getCartId().equals(cart.getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_ITEM);

        restrictionChecker.checkItemList(cart.getItems());
        return cart.getItems();
    }

    public Item create(ItemDto itemDto) {
        Product product = productRepository.findById(itemDto.getProductId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.PRODUCT_NOT_FOUND));

        Restaurant restaurant = restaurantRepository.findById(product.getRestaurantId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        Cart cart = cartRepository.findById(getAuthenticatedUser().getCartId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.CART_NOT_FOUND));

        if (cart.getRestaurantId() != null) {
            Restaurant current = restaurantRepository.findById(cart.getRestaurantId()).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

            if (!current.equals(restaurant))
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_ITEM);
        } else {
            cart.setRestaurantId(restaurant.getId());
        }

        cart.getItems().stream().filter(it -> it.getProduct().getId().equals(itemDto.getProductId())).findFirst().ifPresent(
                (_item) -> {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "the product has already been added to the cart");
                });

        Item item = new Item();
        BeanUtils.copyProperties(itemDto, item);
        calculateItemValues(product, item);
        Item created = itemRepository.save(item);

        cart.getItems().add(created);
        updateCartValues(cart);
        cartRepository.save(cart);

        restrictionChecker.checkItem(created);
        return created;
    }

    private void updateCartValues(Cart cart) {
        Double subtotal = cart.getItems().stream().mapToDouble(Item::getSubtotal).sum();
        Integer quantity = cart.getItems().stream().mapToInt(Item::getQuantity).sum();

        cart.setSubtotal(subtotal);
        cart.setQuantity(quantity);
    }

    private static void calculateItemValues(Product product, Item item) {
        if (item.getQuantity() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be greater than 0");

        Double subtotal = product.getPrice() * item.getQuantity();
        item.setSubtotal(subtotal);
        item.setProduct(product);
    }

    public Item update(ItemDto itemDto) {
        Item item = itemRepository.findById(itemDto.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.ITEM_NOT_FOUND));

        Cart cart = cartRepository.findById(getAuthenticatedUser().getCartId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.CART_NOT_FOUND));

        if (!cart.getItems().contains(item))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_ITEM);

        if (!item.getProduct().getId().equals(itemDto.getProductId()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product cannot be changed");

        Item old = cart.getItems().stream().filter(it -> it.getId().equals(item.getId())).findFirst().orElseThrow(
                () -> new RuntimeException("Unreachable"));

        BeanUtils.copyProperties(itemDto, old);
        calculateItemValues(old.getProduct(), old);
        Item updated = itemRepository.save(old);

        updateCartValues(cart);
        cartRepository.save(cart);

        restrictionChecker.checkItem(updated);
        return updated;
    }

    public void delete(String id) {
        Item item = itemRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.ITEM_NOT_FOUND));

        Cart cart = cartRepository.findById(getAuthenticatedUser().getCartId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.CART_NOT_FOUND));

        if (!cart.getItems().contains(item))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_ITEM);

        cart.getItems().remove(item);
        updateCartValues(cart);
        if (cart.getItems().isEmpty()) cart.setRestaurantId(null);
        cartRepository.save(cart);

        itemRepository.deleteById(id);
    }

    private User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
