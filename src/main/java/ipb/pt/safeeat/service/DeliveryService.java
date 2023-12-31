package ipb.pt.safeeat.service;

import ipb.pt.safeeat.constant.ForbiddenConstant;
import ipb.pt.safeeat.constant.NotFoundConstant;
import ipb.pt.safeeat.dto.DeliveryDto;
import ipb.pt.safeeat.model.Cart;
import ipb.pt.safeeat.model.Delivery;
import ipb.pt.safeeat.model.Restaurant;
import ipb.pt.safeeat.model.User;
import ipb.pt.safeeat.repository.CartRepository;
import ipb.pt.safeeat.repository.DeliveryRepository;
import ipb.pt.safeeat.repository.RestaurantRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class DeliveryService {
    @Autowired
    private DeliveryRepository deliveryRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private CartRepository cartRepository;

    public List<Delivery> findAll() {
        return deliveryRepository.findAll();
    }

    public List<Delivery> findAllByRestaurant(String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        return restaurant.getDeliveries();
    }

    public Delivery findById(String id) {
        return deliveryRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.DELIVERY_NOT_FOUND));
    }

    public Delivery create(DeliveryDto deliveryDto, String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        if (!restaurant.getOwnerId().equals(getAuthenticatedUser().getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_DELIVERY);

        Delivery delivery = new Delivery();
        BeanUtils.copyProperties(deliveryDto, delivery);
        Delivery created = deliveryRepository.save(delivery);

        restaurant.getDeliveries().add(created);
        restaurantRepository.save(restaurant);

        return created;
    }

    public Delivery select(String id) {
        Delivery delivery = deliveryRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.DELIVERY_NOT_FOUND));

        Cart cart = cartRepository.findById(getAuthenticatedUser().getCartId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.CART_NOT_FOUND));

        if (cart.getItems().isEmpty())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot select a delivery in an empty cart");

        Restaurant restaurant = restaurantRepository.findById(cart.getRestaurantId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        if (!restaurant.getDeliveries().contains(delivery))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_DELIVERY);

        cart.setSelectedDelivery(delivery.getId());
        cartRepository.save(cart);

        delivery.setIsSelected(true);
        return delivery;
    }

    public Delivery update(DeliveryDto deliveryDto) {
        Delivery old = deliveryRepository.findById(deliveryDto.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.DELIVERY_NOT_FOUND));

        Restaurant restaurant = restaurantRepository.findAllByDeliveries(old).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        if (!restaurant.getOwnerId().equals(getAuthenticatedUser().getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_DELIVERY);

        if (!restaurant.getDeliveries().contains(old))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_DELIVERY);

        BeanUtils.copyProperties(deliveryDto, old);
        return deliveryRepository.save(old);
    }

    public void delete(String id) {
        Delivery delivery = deliveryRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.DELIVERY_NOT_FOUND));

        Restaurant restaurant = restaurantRepository.findAllByDeliveries(delivery).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        if (!restaurant.getOwnerId().equals(getAuthenticatedUser().getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_DELIVERY);

        if (!restaurant.getDeliveries().contains(delivery))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_DELIVERY);

        restaurant.getDeliveries().remove(delivery);
        restaurantRepository.save(restaurant);

        deliveryRepository.deleteById(id);
    }

    private User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
