package ipb.pt.safeeat.service;

import ipb.pt.safeeat.constants.CartConstants;
import ipb.pt.safeeat.constants.UserConstants;
import ipb.pt.safeeat.model.Cart;
import ipb.pt.safeeat.model.User;
import ipb.pt.safeeat.repository.CartRepository;
import ipb.pt.safeeat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private UserRepository userRepository;

    public List<Cart> getAll() {
        return cartRepository.findAll();
    }

    public Cart findById(String id) {
        return cartRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, CartConstants.NOT_FOUND));
    }

    public Cart create(String userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, UserConstants.NOT_FOUND));

        Cart cart = new Cart();
        Cart created = cartRepository.save(cart);

        user.setCart(created);
        userRepository.save(user);

        return created;
    }

    public Cart reset(String cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, CartConstants.NOT_FOUND));

        cart.getItems().clear();
        cart.setQuantity(0);
        cart.setSubtotal(0.0);

        return cartRepository.save(cart);
    }

    public void delete(String id) {
        cartRepository.deleteById(id);
    }
}
