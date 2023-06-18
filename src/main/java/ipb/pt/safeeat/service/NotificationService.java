package ipb.pt.safeeat.service;

import ipb.pt.safeeat.constants.NotificationConstants;
import ipb.pt.safeeat.constants.OrderConstants;
import ipb.pt.safeeat.model.Notification;
import ipb.pt.safeeat.model.Order;
import ipb.pt.safeeat.repository.NotificationRepository;
import ipb.pt.safeeat.repository.OrderRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    public List<Notification> getAll() {
        try {
            return notificationRepository.findAll();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public Notification findById(String id) {
        return notificationRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotificationConstants.NOT_FOUND));
    }

    public Notification create(Notification notification) {
        Order order = orderRepository.findById(notification.getOrder().getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, OrderConstants.NOT_FOUND));

        notification.setOrder(order);
        notification.setTime(LocalDateTime.now());

        return notificationRepository.save(notification);
    }

    @Transactional
    public List<Notification> createMany(List<Notification> notifications) {
        List<Notification> created = new ArrayList<>();
        for(Notification notification : notifications) {
            created.add(create(notification));
        }

        return created;
    }

    public Notification update(Notification notification) {
        Notification old = notificationRepository.findById(notification.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotificationConstants.NOT_FOUND));

        BeanUtils.copyProperties(notification, old);
        return notificationRepository.save(notification);
    }

    public Notification view(String id) {
        Notification notification = notificationRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotificationConstants.NOT_FOUND));

        notification.setIsViewed(true);
        return notificationRepository.save(notification);
    }

    public void delete(String id) {
        notificationRepository.deleteById(id);
    }
}