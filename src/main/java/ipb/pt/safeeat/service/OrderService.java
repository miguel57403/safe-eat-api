package ipb.pt.safeeat.service;

import ipb.pt.safeeat.constants.*;
import ipb.pt.safeeat.model.*;
import ipb.pt.safeeat.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private DeliveryRepository deliveryRepository;
    @Autowired
    private UserRepository userRepository;

    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    public Order findById(String id) {
        return orderRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, OrderConstants.NOT_FOUND));
    }

    public Order create(Order order) {
        Address address = addressRepository.findById(order.getAddress().getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, AddressConstants.NOT_FOUND));

        Payment payment = paymentRepository.findById(order.getPayment().getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, PaymentConstants.NOT_FOUND));

        Delivery delivery = deliveryRepository.findById(order.getDelivery().getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, DeliveryConstants.NOT_FOUND));

        Restaurant restaurant = restaurantRepository.findById(order.getRestaurant().getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, RestaurantConstants.NOT_FOUND));

        User client = userRepository.findById(order.getClient().getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, UserConstants.NOT_FOUND));

        for(Item item : order.getItems()) {
            itemRepository.findById(item.getId()).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ItemConstants.NOT_FOUND));
        }

        order.setAddress(address);
        order.setPayment(payment);
        order.setDelivery(delivery);
        order.setRestaurant(restaurant);
        order.setClient(client);

        double subtotal = order.getItems().stream().mapToDouble(Item::getSubtotal).sum();
        double total = subtotal + order.getDelivery().getPrice();

        order.setStatus("Registered");
        order.setTime(LocalDateTime.now());
        order.setSubtotal(subtotal);
        order.setTotal(total);

        Order created = orderRepository.save(order);

        restaurant.getOrders().add(created);
        restaurantRepository.save(restaurant);

        client.getOrders().add(created);
        userRepository.save(client);

        return created;
    }

    @Transactional
    public List<Order> createMany(List<Order> orders) {
        List<Order> created = new ArrayList<>();
        for(Order order : orders) {
            created.add(create(order));
        }

        return created;
    }

    public Order update(Order order) {
        Order old = orderRepository.findById(order.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, OrderConstants.NOT_FOUND));

        if (!order.getStatus().equals(old.getStatus())) {
            old.setStatus(order.getStatus());
        }

        return orderRepository.save(order);
    }

    public void delete(String id) {
        orderRepository.deleteById(id);
    }
}
