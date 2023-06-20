package ipb.pt.safeeat.service;

import ipb.pt.safeeat.constant.CartConstants;
import ipb.pt.safeeat.model.Cart;
import ipb.pt.safeeat.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;

    public List<Cart> getAll() {
        return cartRepository.findAll();
    }

    public Cart findById(String id) {
        return cartRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, CartConstants.NOT_FOUND));
    }

    public Boolean isBuying(String id) {
        Cart cart = cartRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, CartConstants.NOT_FOUND));

        return cart.getItems().size() != 0;
    }

    public Cart empty(String cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, CartConstants.NOT_FOUND));

        cart.getItems().clear();
        cart.setQuantity(0);
        cart.setSubtotal(0.0);

        return cartRepository.save(cart);
    }
}
