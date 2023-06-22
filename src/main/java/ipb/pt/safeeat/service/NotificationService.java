package ipb.pt.safeeat.service;

import ipb.pt.safeeat.utility.NotFoundConstants;
import ipb.pt.safeeat.dto.NotificationDto;
import ipb.pt.safeeat.model.Notification;
import ipb.pt.safeeat.model.Order;
import ipb.pt.safeeat.model.User;
import ipb.pt.safeeat.repository.NotificationRepository;
import ipb.pt.safeeat.repository.OrderRepository;
import ipb.pt.safeeat.repository.UserRepository;
import org.springframework.beans.BeanUtils;
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
        return notificationRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.NOTIFICATION_NOT_FOUND));
    }

    public List<Notification> findAllByUser(String id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.USER_NOT_FOUND));

        return user.getNotifications();
    }

    public Notification create(NotificationDto notificationDto, String userId) {
        Order order = orderRepository.findById(notificationDto.getOrderId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.ORDER_NOT_FOUND));

        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.USER_NOT_FOUND));

        Notification notification = new Notification();
        BeanUtils.copyProperties(notificationDto, notification);

        notification.setOrder(order);
        notification.setTime(LocalDateTime.now());

        Notification created = notificationRepository.save(notification);

        user.getNotifications().add(created);
        userRepository.save(user);

        return created;
    }

    @Transactional
    public List<Notification> createMany(List<NotificationDto> notificationDtos, String userId) {
        List<Notification> created = new ArrayList<>();
        for (NotificationDto notificationDto : notificationDtos) {
            created.add(create(notificationDto, userId));
        }

        return created;
    }

    public Notification update(NotificationDto notificationDto) {
        Notification old = notificationRepository.findById(notificationDto.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.NOTIFICATION_NOT_FOUND));

        BeanUtils.copyProperties(notificationDto, old);
        return notificationRepository.save(old);
    }

    public Notification view(String id) {
        Notification notification = notificationRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.NOTIFICATION_NOT_FOUND));

        notification.setIsViewed(true);
        return notificationRepository.save(notification);
    }

    public void delete(String id, String userId) {
        Notification notification = notificationRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.NOTIFICATION_NOT_FOUND));

        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            user.get().getNotifications().remove(notification);
            userRepository.save(user.get());
        }

        notificationRepository.deleteById(id);
    }
}
