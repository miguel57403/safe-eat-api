package ipb.pt.safeeat.service;

import ipb.pt.safeeat.constants.CartConstants;
import ipb.pt.safeeat.constants.ItemConstants;
import ipb.pt.safeeat.constants.ProductConstants;
import ipb.pt.safeeat.model.Cart;
import ipb.pt.safeeat.model.Item;
import ipb.pt.safeeat.model.Product;
import ipb.pt.safeeat.repository.CartRepository;
import ipb.pt.safeeat.repository.ItemRepository;
import ipb.pt.safeeat.repository.ProductRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    public List<Item> getAll() {
        return itemRepository.findAll();
    }

    public Item findById(String id) {
        return itemRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ItemConstants.NOT_FOUND));
    }

    public Item create(Item item, String cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, CartConstants.NOT_FOUND));

        Product product = productRepository.findById(item.getProduct().getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ProductConstants.NOT_FOUND));

        double subtotal = product.getPrice() * item.getQuantity();
        item.setSubtotal(subtotal);
        item.setProduct(product);

        Item created = itemRepository.save(item);

        cart.getItems().add(created);
        cartRepository.save(cart);

        return created;
    }

    @Transactional
    public List<Item> createMany(List<Item> items, String cartId) {
        List<Item> created = new ArrayList<>();
        for(Item item : items) {
            created.add(create(item, cartId));
        }

        return created;
    }

    public Item update(Item item) {
        Item old = itemRepository.findById(item.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ItemConstants.NOT_FOUND));

        BeanUtils.copyProperties(item, old);
        return itemRepository.save(item);
    }

    public void delete(String id) {
        itemRepository.deleteById(id);
    }
}
