package ipb.pt.safeeat.service;

import ipb.pt.safeeat.dto.NotificationDto;
import ipb.pt.safeeat.model.Notification;
import ipb.pt.safeeat.model.Order;
import ipb.pt.safeeat.model.Restaurant;
import ipb.pt.safeeat.model.User;
import ipb.pt.safeeat.repository.NotificationRepository;
import ipb.pt.safeeat.repository.OrderRepository;
import ipb.pt.safeeat.repository.UserRepository;
import ipb.pt.safeeat.utility.NotAllowedConstants;
import ipb.pt.safeeat.utility.NotFoundConstants;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Notification> findAll() {
        return notificationRepository.findAll();
    }

    public Notification findById(String id) {
        Notification notification = notificationRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.NOTIFICATION_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!user.isAdmin() && !user.getNotifications().contains(notification))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, NotAllowedConstants.FORBIDDEN_NOTIFICATION);

        return notification;
    }

    public List<Notification> findAllByUser(String id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.USER_NOT_FOUND));

        return user.getNotifications();
    }

    public Notification create(NotificationDto notificationDto) {
        Order order = orderRepository.findById(notificationDto.getOrderId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.ORDER_NOT_FOUND));

        User owner = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Restaurant restaurant = order.getRestaurant();

        if(!owner.getRestaurants().contains(restaurant))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.RESTAURANT_NOT_FOUND);

        User client = userRepository.findById(order.getClient().getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.USER_NOT_FOUND));

        Notification notification = new Notification();
        BeanUtils.copyProperties(notificationDto, notification);

        notification.setOrder(order);
        notification.setTime(LocalDateTime.now());

        Notification created = notificationRepository.save(notification);

        client.getNotifications().add(created);
        userRepository.save(client);

        return created;
    }

    @Transactional
    public List<Notification> createMany(List<NotificationDto> notificationDtos) {
        List<Notification> created = new ArrayList<>();
        for (NotificationDto notificationDto : notificationDtos) {
            created.add(create(notificationDto));
        }

        return created;
    }

    public Notification update(NotificationDto notificationDto) {
        Notification old = notificationRepository.findById(notificationDto.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.NOTIFICATION_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!user.getNotifications().contains(old))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.NOTIFICATION_NOT_FOUND);

        BeanUtils.copyProperties(notificationDto, old);
        return notificationRepository.save(old);
    }

    public Notification view(String id) {
        Notification notification = notificationRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.NOTIFICATION_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!user.getNotifications().contains(notification))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.NOTIFICATION_NOT_FOUND);

        notification.setIsViewed(true);
        return notificationRepository.save(notification);
    }

    public void delete(String id) {
        Notification notification = notificationRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.NOTIFICATION_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!user.getNotifications().contains(notification))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.NOTIFICATION_NOT_FOUND);

        user.getNotifications().remove(notification);
        userRepository.save(user);
        notificationRepository.deleteById(id);
    }
}
