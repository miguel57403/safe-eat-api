package ipb.pt.safeeat.service;

import ipb.pt.safeeat.component.RestrictionCheckerComponent;
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
    private RestrictionCheckerComponent restrictionCheckerComponent;

    public List<Item> findAll() {
        List<Item> items = itemRepository.findAll();
        restrictionCheckerComponent.checkItemList(items);
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

        restrictionCheckerComponent.checkItem(item);
        return item;
    }

    public List<Item> findAllByCart(String cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.CART_NOT_FOUND));

        if (!getAuthenticatedUser().isAdmin() && !getAuthenticatedUser().getCartId().equals(cart.getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_ITEM);

        restrictionCheckerComponent.checkItemList(cart.getItems());
        return cart.getItems();
    }

    public Item create(ItemDto itemDto) {
        Product product = productRepository.findById(itemDto.getProductId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.PRODUCT_NOT_FOUND));

        Restaurant restaurant = restaurantRepository.findById(product.getRestaurantId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        Cart cart = cartRepository.findById(getAuthenticatedUser().getCartId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.CART_NOT_FOUND));

        if (!cart.getItems().isEmpty()) {
            Restaurant current = restaurantRepository.findById(cart.getItems().get(0).getProduct().getRestaurantId()).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

            if (!current.equals(restaurant))
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_ITEM);
        }

        Item item = new Item();
        BeanUtils.copyProperties(itemDto, item);
        calculateValues(product, item);
        Item created = itemRepository.save(item);

        updateCartValues(cart, created);

        restrictionCheckerComponent.checkItem(created);
        return created;
    }

    private void updateCartValues(Cart cart, Item created) {
        cart.getItems().add(created);

        Double subtotal = cart.getItems().stream().mapToDouble(Item::getSubtotal).sum();
        Integer quantity = cart.getItems().stream().mapToInt(Item::getQuantity).sum();

        cart.setSubtotal(subtotal);
        cart.setQuantity(quantity);
        cartRepository.save(cart);
    }

    private static void calculateValues(Product product, Item item) {
        if (item.getQuantity() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be greater than 0");

        Double subtotal = product.getPrice() * item.getQuantity();
        item.setSubtotal(subtotal);
        item.setProduct(product);
    }

    public Item update(ItemDto itemDto) {
        Item old = itemRepository.findById(itemDto.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.ITEM_NOT_FOUND));

        Cart cart = cartRepository.findById(getAuthenticatedUser().getCartId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.CART_NOT_FOUND));

        if (!cart.getItems().contains(old))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_ITEM);

        if (!old.getProduct().getId().equals(itemDto.getProductId()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product cannot be changed");

        BeanUtils.copyProperties(itemDto, old);
        calculateValues(old.getProduct(), old);
        Item updated = itemRepository.save(old);

        updateCartValues(cart, updated);

        restrictionCheckerComponent.checkItem(updated);
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
        cartRepository.save(cart);

        itemRepository.deleteById(id);
    }

    private User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
