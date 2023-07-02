package ipb.pt.safeeat.service;

import ipb.pt.safeeat.constant.ForbiddenConstant;
import ipb.pt.safeeat.constant.NotFoundConstant;
import ipb.pt.safeeat.model.Notification;
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

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!user.isAdmin() && !user.getNotifications().contains(notification))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_NOTIFICATION);

        return notification;
    }

    public List<Notification> findAllByUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.USER_NOT_FOUND));

        User current = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!current.isAdmin() && !current.equals(user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_NOTIFICATION);

        return user.getNotifications();
    }

    // TODO: arrumar isso
    public List<Notification> findAllByRestaurant(String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        User current = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!current.isAdmin() && !current.equals(restaurant.getOwner()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_NOTIFICATION);

        return null;
    }

    // TODO:
    public Notification view(String id) {
        Notification notification = notificationRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.NOTIFICATION_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!notification.getRestaurant().getOwner().equals(user) && !user.getNotifications().contains(notification))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_NOTIFICATION);

        notification.setIsViewed(true);
        notificationRepository.save(notification);

        return notification;
    }

    public void delete(String id) {
        System.out.println("deleting");
        Notification notification = notificationRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.NOTIFICATION_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!notification.getRestaurant().getOwner().equals(user) && !user.getNotifications().contains(notification))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_NOTIFICATION);

        user.getNotifications().remove(notification);
        userRepository.save(user);
        notificationRepository.deleteById(id);
    }
}
