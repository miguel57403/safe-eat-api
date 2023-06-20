package ipb.pt.safeeat.service;

import ipb.pt.safeeat.constant.NotificationConstants;
import ipb.pt.safeeat.constant.OrderConstants;
import ipb.pt.safeeat.dto.NotificationDto;
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
        return notificationRepository.findAll();
    }

    public Notification findById(String id) {
        return notificationRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotificationConstants.NOT_FOUND));
    }

    public Notification create(NotificationDto notificationDto) {
        Order order = orderRepository.findById(notificationDto.getOrderId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, OrderConstants.NOT_FOUND));

        Notification notification = new Notification();
        BeanUtils.copyProperties(notificationDto, notification);

        notification.setOrder(order);
        notification.setTime(LocalDateTime.now());

        return notificationRepository.save(notification);
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
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotificationConstants.NOT_FOUND));

        BeanUtils.copyProperties(notificationDto, old);
        return notificationRepository.save(old);
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
