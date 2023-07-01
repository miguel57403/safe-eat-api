package ipb.pt.safeeat.service;

import ipb.pt.safeeat.constant.ForbiddenConstant;
import ipb.pt.safeeat.constant.NotFoundConstant;
import ipb.pt.safeeat.dto.OrderDraftDto;
import ipb.pt.safeeat.dto.OrderDto;
import ipb.pt.safeeat.model.*;
import ipb.pt.safeeat.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
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
    private RestaurantRepository restaurantRepository;
    @Autowired
    private DeliveryRepository deliveryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private FeedbackRepository feedbackRepository;

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order findById(String id) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.ORDER_NOT_FOUND));

        Restaurant restaurant = order.getRestaurant();
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!user.isAdmin() && !restaurant.getOwner().equals(user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_ORDER);

        return order;
    }

    public List<Order> findAllByUser(String id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.USER_NOT_FOUND));

        User current = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!current.isAdmin() && !current.equals(user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_ORDER);

        return user.getOrders();
    }

    public List<Order> findAllByRestaurant(String id) {
        Restaurant restaurant = restaurantRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!user.isAdmin() && !restaurant.getOwner().equals(user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_ORDER);

        return restaurant.getOrders();
    }

    public Order create(OrderDto orderDto) {
        User client = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Cart cart = client.getCart();
        List<Item> items = cart.getItems();

        if(items.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");

        Restaurant restaurant = restaurantRepository.findByProducts(items.get(0).getProduct()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        Delivery delivery = deliveryRepository.findById(orderDto.getDeliveryId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.DELIVERY_NOT_FOUND));

        Payment payment = paymentRepository.findById(orderDto.getPaymentId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.PAYMENT_NOT_FOUND));

        Address address = addressRepository.findById(orderDto.getAddressId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.ADDRESS_NOT_FOUND));

        if (!restaurant.getDeliveries().contains(delivery))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Delivery option not available");

        if (!client.getPayments().contains(payment))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment option not available");

        if (!client.getAddresses().contains(address))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Address not available");

        Order order = new Order();

        order.setAddress(address);
        order.setPayment(payment);
        order.setDelivery(delivery);
        order.setRestaurant(restaurant);
        order.setClient(client);
        order.setItems(items);

        Double subtotal = order.getItems().stream().mapToDouble(Item::getSubtotal).sum();
        Double total = subtotal + order.getDelivery().getPrice();
        Integer quantity = order.getItems().stream().mapToInt(Item::getQuantity).sum();

        order.setStatus("REGISTERED");
        order.setTime(LocalDateTime.now());
        order.setQuantity(quantity);
        order.setSubtotal(subtotal);
        order.setTotal(total);

        Order created = orderRepository.save(order);

        Notification notification = new Notification();

        notification.setTime(LocalDateTime.now());
        notification.setContent("New order from " + client.getName());
        notification.setOrder(created);
        notification.setIsViewed(false);

        Notification newNotification = notificationRepository.save(notification);

        restaurant.getNotifications().add(newNotification);
        restaurant.getOrders().add(created);
        restaurantRepository.save(restaurant);

        client.getOrders().add(created);
        userRepository.save(client);

        return created;
    }

    public OrderDraftDto getOrderDraft() {
        User client = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Cart cart = client.getCart();
        List<Item> items = cart.getItems();

        if(items.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");

        Restaurant restaurant = restaurantRepository.findByProducts(items.get(0).getProduct()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        Double subtotal = cart.getItems().stream().mapToDouble(Item::getSubtotal).sum();
        Integer quantity = cart.getItems().stream().mapToInt(Item::getQuantity).sum();

        OrderDraftDto orderDraftDto = new OrderDraftDto();

        orderDraftDto.setAddresses(client.getAddresses());
        orderDraftDto.setPayments(client.getPayments());
        orderDraftDto.setDeliveries(restaurant.getDeliveries());
        orderDraftDto.setSubtotal(subtotal);
        orderDraftDto.setQuantity(quantity);

        return orderDraftDto;
    }

    public Order updateStatus(String id, String status) {
        Order old = orderRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.ORDER_NOT_FOUND));

        Restaurant restaurant = restaurantRepository.findByOrders(old).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        User client = userRepository.findByOrders(old).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.USER_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!restaurant.getOwner().equals(user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_ORDER);

        Notification notification = new Notification();

        switch (status) {
            case "PREPARING" -> notification.setContent("Your order is being prepared");
            case "TRANSPORTING" -> notification.setContent("Your order is being transported");
            case "DELIVERED" -> notification.setContent("Your order has been delivered");
            case "CANCELED" -> notification.setContent("Your order has been cancelled");
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status");
        }

        old.setStatus(status);
        Order updated = orderRepository.save(old);

        notification.setTime(LocalDateTime.now());
        notification.setOrder(updated);
        notification.setIsViewed(false);

        Notification newNotification = notificationRepository.save(notification);

        client.getNotifications().add(newNotification);
        userRepository.save(client);

        return updated;
    }

    public void delete(String id) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.ORDER_NOT_FOUND));

        Optional<User> user = userRepository.findByOrders(order);
        Optional<Restaurant> restaurant = restaurantRepository.findByOrders(order);
        Feedback feedback = order.getFeedback();

        List<Notification> notifications = notificationRepository.findAllByOrder(order);

        user.ifPresent(value -> value.getOrders().remove(order));
        restaurant.ifPresent(value -> value.getOrders().remove(order));

        user.ifPresent(value -> userRepository.save(value));
        restaurant.ifPresent(value -> restaurantRepository.save(value));

        if (feedback != null) {
            feedbackRepository.delete(feedback);
        }

        notificationRepository.deleteAll(notifications);
        orderRepository.deleteById(id);
    }
}