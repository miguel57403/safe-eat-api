package ipb.pt.safeeat.service;

import ipb.pt.safeeat.model.Item;
import ipb.pt.safeeat.utility.ForbiddenConstants;
import ipb.pt.safeeat.utility.NotFoundConstants;
import ipb.pt.safeeat.model.Cart;
import ipb.pt.safeeat.model.User;
import ipb.pt.safeeat.repository.CartRepository;
import ipb.pt.safeeat.repository.UserRepository;
import ipb.pt.safeeat.utility.RestrictionChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RestrictionChecker restrictionChecker;

    public List<Cart> findAll() {
        return cartRepository.findAll();
    }

    public Cart findById(String id) {
        Cart cart = cartRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.CART_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!user.isAdmin() && !user.getCart().equals(cart))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstants.FORBIDDEN_CART);

        for (Item item : cart.getItems()) {
            restrictionChecker.checkProduct(item.getProduct());
        }

        return cart;
    }

    public Cart findByUser(String id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.USER_NOT_FOUND));

        User current = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!current.isAdmin() && !current.equals(user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstants.FORBIDDEN_CART);

        for (Item item : user.getCart().getItems()) {
            restrictionChecker.checkProduct(item.getProduct());
        }

        return user.getCart();
    }

    public Boolean isMineEmpty() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Cart cart = user.getCart();
        return cart.getItems().size() == 0;
    }

    public Cart emptyMine() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Cart cart = user.getCart();

        cart.getItems().clear();
        cart.setQuantity(0);
        cart.setSubtotal(0.0);

        return cartRepository.save(cart);
    }
}
