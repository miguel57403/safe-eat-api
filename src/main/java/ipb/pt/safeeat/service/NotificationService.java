package ipb.pt.safeeat.service;

import ipb.pt.safeeat.constant.ForbiddenConstant;
import ipb.pt.safeeat.constant.NotFoundConstant;
import ipb.pt.safeeat.constant.OrderStatusConstant;
import ipb.pt.safeeat.model.Notification;
import ipb.pt.safeeat.model.Order;
import ipb.pt.safeeat.model.Restaurant;
import ipb.pt.safeeat.model.User;
import ipb.pt.safeeat.repository.NotificationRepository;
import ipb.pt.safeeat.repository.RestaurantRepository;
import ipb.pt.safeeat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;

    public List<Notification> findAll() {
        return notificationRepository.findAll();
    }

    public Notification findById(String id) {
        Notification notification = notificationRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.NOTIFICATION_NOT_FOUND));

        User user = getAuthenticatedUser();

        if (!user.isAdmin() && notification.getClient().equals(user) && notification.getRestaurant().getOwnerId().equals(user.getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_NOTIFICATION);

        return notification;
    }

    public List<Notification> findAllByUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.USER_NOT_FOUND));

        if (!getAuthenticatedUser().isAdmin() && !getAuthenticatedUser().equals(user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_NOTIFICATION);

        return notificationRepository.findAllByClientAndReceiver(user, "USER");
    }

    public List<Notification> findAllByRestaurant(String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        if (!getAuthenticatedUser().isAdmin() && !getAuthenticatedUser().getId().equals(restaurant.getOwnerId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_NOTIFICATION);

        return notificationRepository.findAllByRestaurantAndReceiver(restaurant, "RESTAURANT");
    }

    public Notification view(String id) {
        Notification notification = notificationRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.NOTIFICATION_NOT_FOUND));

        User user = getAuthenticatedUser();

        if (!notification.getRestaurant().getOwnerId().equals(user.getId()) && !notification.getClient().equals(user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_NOTIFICATION);

        notification.setIsViewed(true);
        notificationRepository.save(notification);

        return notification;
    }

    public void delete(String id) {
        Notification notification = notificationRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.NOTIFICATION_NOT_FOUND));

        User user = getAuthenticatedUser();

        if (!notification.getRestaurant().getOwnerId().equals(user.getId()) && !notification.getClient().equals(user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_NOTIFICATION);

        notificationRepository.deleteById(id);
    }

    private User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public void notifyOrderCreated(User client, Restaurant restaurant, Order order) {
        Notification notification = new Notification();
        notification.setContent("New order from " + client.getName());
        notification.setTime(LocalDateTime.now());
        notification.setClient(client);
        notification.setRestaurant(restaurant);
        notification.setOrderId(order.getId());
        notification.setReceiver("RESTAURANT");
        notification.setIsViewed(false);
        notificationRepository.save(notification);
        notification.setContent("New order to " + restaurant.getName());
        notification.setReceiver("USER");
        notificationRepository.save(notification);
    }

    public void notifyOrderUpdated(Order old, Order updated, String status) {
        Notification notification = new Notification();

        switch (status) {
            case OrderStatusConstant.PREPARING -> notification.setContent("Your order is being prepared");
            case OrderStatusConstant.TRANSPORTING -> notification.setContent("Your order is being transported");
            case OrderStatusConstant.DELIVERED -> notification.setContent("Your order has been delivered");
            case OrderStatusConstant.CANCELED -> notification.setContent("Your order has been cancelled");
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status");
        }

        notification.setTime(LocalDateTime.now());
        notification.setClient(old.getClient());
        notification.setRestaurant(old.getRestaurant());
        notification.setOrderId(updated.getId());
        notification.setReceiver("USER");
        notification.setIsViewed(false);
        notificationRepository.save(notification);
    }

    public void notifyFeedbackCreated(Order old, Order updated) {
        Notification notification = new Notification();
        notification.setContent("Feedback received from " + getAuthenticatedUser().getName());
        notification.setTime(LocalDateTime.now());
        notification.setClient(old.getClient());
        notification.setRestaurant(old.getRestaurant());
        notification.setOrderId(updated.getId());
        notification.setReceiver("RESTAURANT");
        notification.setIsViewed(false);
        notificationRepository.save(notification);
    }
}
