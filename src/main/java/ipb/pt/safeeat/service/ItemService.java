package ipb.pt.safeeat.service;

import ipb.pt.safeeat.dto.ItemDto;
import ipb.pt.safeeat.model.Cart;
import ipb.pt.safeeat.model.Item;
import ipb.pt.safeeat.model.Product;
import ipb.pt.safeeat.model.User;
import ipb.pt.safeeat.repository.CartRepository;
import ipb.pt.safeeat.repository.ItemRepository;
import ipb.pt.safeeat.repository.ProductRepository;
import ipb.pt.safeeat.utility.ForbiddenConstants;
import ipb.pt.safeeat.utility.NotFoundConstants;
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

@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private RestrictionChecker restrictionChecker;

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Item findById(String id) {
        Item item = itemRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.ITEM_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!user.isAdmin() && !user.getCart().getItems().contains(item))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstants.FORBIDDEN_ITEM);

        restrictionChecker.checkProduct(item.getProduct());
        return item;
    }

    public List<Item> findAllByCartId(String cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.CART_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!user.isAdmin() && !user.getCart().equals(cart))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstants.FORBIDDEN_ITEM);

        for (Item item : cart.getItems()) {
            restrictionChecker.checkProduct(item.getProduct());
        }

        return cart.getItems();
    }

    public Item create(ItemDto itemDto) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Cart cart = user.getCart();

        Product product = productRepository.findById(itemDto.getProductId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.PRODUCT_NOT_FOUND));

        Item item = new Item();
        BeanUtils.copyProperties(itemDto, item);

        calculateValues(product, item);
        Item created = itemRepository.save(item);

        cart.getItems().add(created);
        cartRepository.save(cart);

        return created;
    }

    private static void calculateValues(Product product, Item item) {
        if (item.getQuantity() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be greater than 0");

        double subtotal = product.getPrice() * item.getQuantity();
        item.setSubtotal(subtotal);
        item.setProduct(product);
    }

    @Transactional
    public List<Item> createMany(List<ItemDto> itemDtos) {
        List<Item> created = new ArrayList<>();
        for (ItemDto itemDto : itemDtos) {
            created.add(create(itemDto));
        }

        return created;
    }

    public Item update(ItemDto itemDto) {
        Item old = itemRepository.findById(itemDto.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.ITEM_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Cart cart = user.getCart();

        if (!cart.getItems().contains(old))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstants.FORBIDDEN_ITEM);

        Product product = productRepository.findById(itemDto.getProductId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.PRODUCT_NOT_FOUND));

        BeanUtils.copyProperties(itemDto, old);
        calculateValues(product, old);

        return itemRepository.save(old);
    }

    public void delete(String id) {
        Item item = itemRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.ITEM_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Cart cart = user.getCart();

        if (!cart.getItems().contains(item))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstants.FORBIDDEN_ITEM);

        cart.getItems().remove(item);
        cartRepository.save(cart);

        itemRepository.deleteById(id);
    }
}
