package ipb.pt.safeeat.service;

import ipb.pt.safeeat.constants.NotFoundConstants;
import ipb.pt.safeeat.dto.OrderDto;
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
import java.util.Optional;

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
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.ORDER_NOT_FOUND));
    }

    public List<Order> findAllByUser(String id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.USER_NOT_FOUND));

        return user.getOrders();
    }

    public Order create(OrderDto orderDto) {
        Address address = addressRepository.findById(orderDto.getAddressId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.ADDRESS_NOT_FOUND));

        Payment payment = paymentRepository.findById(orderDto.getPaymentId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.PAYMENT_NOT_FOUND));

        Delivery delivery = deliveryRepository.findById(orderDto.getDeliveryId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.DELIVERY_NOT_FOUND));

        Restaurant restaurant = restaurantRepository.findById(orderDto.getRestaurantId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.RESTAURANT_NOT_FOUND));

        User client = userRepository.findById(orderDto.getClientId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.USER_NOT_FOUND));

        List<Item> items = new ArrayList<>();
        for (String itemId : orderDto.getItemIds()) {
            items.add(itemRepository.findById(itemId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.ITEM_NOT_FOUND)));
        }

        Order order = new Order();

        order.setAddress(address);
        order.setPayment(payment);
        order.setDelivery(delivery);
        order.setRestaurant(restaurant);
        order.setClient(client);
        order.setItems(items);

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
    public List<Order> createMany(List<OrderDto> orderDtos) {
        List<Order> created = new ArrayList<>();
        for (OrderDto orderDto : orderDtos) {
            created.add(create(orderDto));
        }

        return created;
    }

    public Order updateStatus(String id, String status) {
        Order old = orderRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.ORDER_NOT_FOUND));

        old.setStatus(status);
        return orderRepository.save(old);
    }

    public void delete(String id) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.ORDER_NOT_FOUND));

        Optional<User> user = userRepository.findById(order.getClient().getId());
        Optional<Restaurant> restaurant = restaurantRepository.findById(order.getRestaurant().getId());

        if (user.isPresent()) {
            user.get().getOrders().remove(order);
            userRepository.save(user.get());
        }

        if (restaurant.isPresent()) {
            restaurant.get().getOrders().remove(order);
            restaurantRepository.save(restaurant.get());
        }

        orderRepository.deleteById(id);
    }
}
