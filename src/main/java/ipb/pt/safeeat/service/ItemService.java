package ipb.pt.safeeat.service;

import ipb.pt.safeeat.utility.NotFoundConstants;
import ipb.pt.safeeat.dto.ItemDto;
import ipb.pt.safeeat.model.Cart;
import ipb.pt.safeeat.model.Item;
import ipb.pt.safeeat.model.Product;
import ipb.pt.safeeat.repository.CartRepository;
import ipb.pt.safeeat.repository.ItemRepository;
import ipb.pt.safeeat.repository.ProductRepository;
import ipb.pt.safeeat.utility.RestrictionChecker;
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
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartRepository cartRepository;

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Item findById(String id) {
        Item item = itemRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.ITEM_NOT_FOUND));

        RestrictionChecker.checkProduct(item.getProduct());
        return item;
    }

    public List<Item> findByCartId(String cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.CART_NOT_FOUND));

        for(Item item : cart.getItems()) {
            RestrictionChecker.checkProduct(item.getProduct());
        }

        return cart.getItems();
    }

    public Item create(ItemDto itemDto, String cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.CART_NOT_FOUND));

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
        if(item.getQuantity() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be greater than 0");

        double subtotal = product.getPrice() * item.getQuantity();
        item.setSubtotal(subtotal);
        item.setProduct(product);
    }

    @Transactional
    public List<Item> createMany(List<ItemDto> itemDtos, String cartId) {
        List<Item> created = new ArrayList<>();
        for (ItemDto itemDto : itemDtos) {
            created.add(create(itemDto, cartId));
        }

        return created;
    }

    public Item update(ItemDto itemDto) {
        Item old = itemRepository.findById(itemDto.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.ITEM_NOT_FOUND));

        Product product = productRepository.findById(itemDto.getProductId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.PRODUCT_NOT_FOUND));

        BeanUtils.copyProperties(itemDto, old);
        calculateValues(product, old);

        return itemRepository.save(old);
    }

    public void delete(String id, String cartId) {
        Item item = itemRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.ITEM_NOT_FOUND));

        Optional<Cart> cart = cartRepository.findById(cartId);

        if (cart.isPresent()) {
            cart.get().getItems().remove(item);
            cartRepository.save(cart.get());
        }

        itemRepository.deleteById(id);
    }
}
