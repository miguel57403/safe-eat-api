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
    @Autowired
    private CartRepository cartRepository;

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order findById(String id) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.ORDER_NOT_FOUND));

        User user = getAuthenticatedUser();

        if (!user.isAdmin() && order.getClient().equals(user) && order.getRestaurant().getOwnerId().equals(user.getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_ORDER);

        return order;
    }

    public List<Order> findAllByUser(String id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.USER_NOT_FOUND));

        if (!getAuthenticatedUser().isAdmin() && !getAuthenticatedUser().equals(user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_ORDER);

        return orderRepository.findAllByClient(user);
    }

    public List<Order> findAllByRestaurant(String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        if (!getAuthenticatedUser().isAdmin() && !restaurant.getOwnerId().equals(getAuthenticatedUser().getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_ORDER);

        return orderRepository.findAllByRestaurant(restaurant);
    }

    public Order create(OrderDto orderDto) {
        User client = getAuthenticatedUser();

        Cart cart = cartRepository.findById(getAuthenticatedUser().getCartId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.CART_NOT_FOUND));

        List<Item> items = cart.getItems();

        if (items.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");

        Restaurant restaurant = restaurantRepository.findById(items.get(0).getProduct().getRestaurantId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        Delivery delivery = deliveryRepository.findById(orderDto.getDeliveryId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.DELIVERY_NOT_FOUND));

        Payment payment = paymentRepository.findById(orderDto.getPaymentId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.PAYMENT_NOT_FOUND));

        Address address = addressRepository.findById(orderDto.getAddressId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.ADDRESS_NOT_FOUND));

        if (!restaurant.getDeliveries().contains(delivery))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Delivery option not available");

        if (!payment.getUserId().equals(client.getId()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment option not available");

        if (!address.getUserId().equals(client.getId()))
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
        notification.setContent("New order from " + client.getName());
        notification.setTime(LocalDateTime.now());
        notification.setClient(client);
        notification.setRestaurant(restaurant);
        notification.setOrderId(order.getId());
        notification.setReceiver("RESTAURANT");
        notification.setIsViewed(false);
        notificationRepository.save(notification);

        return created;
    }

    public OrderDraftDto getOrderDraft() {
        User client = getAuthenticatedUser();

        Cart cart = cartRepository.findById(getAuthenticatedUser().getCartId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.CART_NOT_FOUND));

        List<Item> items = cart.getItems();

        if (items.isEmpty())
            return null;

        Restaurant restaurant = restaurantRepository.findById(items.get(0).getProduct().getRestaurantId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        Double subtotal = cart.getItems().stream().mapToDouble(Item::getSubtotal).sum();
        Integer quantity = cart.getItems().stream().mapToInt(Item::getQuantity).sum();

        List<Address> addresses = addressRepository.findAllByUserId(client.getId());
        List<Payment> payments = paymentRepository.findAllByUserId(client.getId());

        boolean found = false;
        for (Delivery delivery : restaurant.getDeliveries()) {
            if (delivery.getId().equals(cart.getSelectedDelivery())) {
                delivery.setIsSelected(true);
                found = true;
            } else {
                delivery.setIsSelected(false);
            }
        }

        if (!found && !restaurant.getDeliveries().isEmpty() && !payments.isEmpty()) {
            payments.get(0).setIsSelected(true);
        }

        OrderDraftDto orderDraftDto = new OrderDraftDto();
        orderDraftDto.setAddresses(addresses);
        orderDraftDto.setPayments(payments);
        orderDraftDto.setDeliveries(restaurant.getDeliveries());
        orderDraftDto.setSubtotal(subtotal);
        orderDraftDto.setQuantity(quantity);
        return orderDraftDto;
    }

    public Order updateStatus(String id, String status) {
        Order old = orderRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.ORDER_NOT_FOUND));

        if (!old.getRestaurant().getOwnerId().equals(getAuthenticatedUser().getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_ORDER);

        old.setStatus(status);
        Order updated = orderRepository.save(old);

        Notification notification = new Notification();

        switch (status) {
            case "PREPARING" -> notification.setContent("Your order is being prepared");
            case "TRANSPORTING" -> notification.setContent("Your order is being transported");
            case "DELIVERED" -> notification.setContent("Your order has been delivered");
            case "CANCELED" -> notification.setContent("Your order has been cancelled");
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status");
        }

        notification.setTime(LocalDateTime.now());
        notification.setClient(old.getClient());
        notification.setRestaurant(old.getRestaurant());
        notification.setOrderId(updated.getId());
        notification.setReceiver("USER");
        notification.setIsViewed(false);
        notificationRepository.save(notification);

        return updated;
    }

    public void delete(String id) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.ORDER_NOT_FOUND));

        Feedback feedback = feedbackRepository.findById(order.getFeedbackId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.FEEDBACK_NOT_FOUND));

        if (feedback != null) {
            feedbackRepository.delete(feedback);
        }

        orderRepository.deleteById(id);
    }

    private User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
