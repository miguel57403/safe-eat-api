package ipb.pt.safeeat.service;

import ipb.pt.safeeat.dto.FeedbackDto;
import ipb.pt.safeeat.model.*;
import ipb.pt.safeeat.repository.FeedbackRepository;
import ipb.pt.safeeat.repository.NotificationRepository;
import ipb.pt.safeeat.repository.OrderRepository;
import ipb.pt.safeeat.repository.RestaurantRepository;
import ipb.pt.safeeat.constant.ForbiddenConstant;
import ipb.pt.safeeat.constant.NotFoundConstant;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeedbackService {
    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private NotificationRepository notificationRepository;

    public List<Feedback> findAll() {
        return feedbackRepository.findAll();
    }

    public Feedback findById(String id) {
        Feedback feedback = feedbackRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.FEEDBACK_NOT_FOUND));

        Order order = orderRepository.findByFeedback(feedback).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.ORDER_NOT_FOUND));

        Restaurant restaurant = restaurantRepository.findByOrders(order).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!user.isAdmin() && !restaurant.getOwner().equals(user) && !order.getClient().equals(user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_FEEDBACK);

        return feedback;
    }

    public Feedback findByOrder(String orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.ORDER_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!user.isAdmin() && !user.getOrders().contains(order))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_FEEDBACK);

        return order.getFeedback();
    }

    public Feedback create(FeedbackDto feedbackDto, String orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.ORDER_NOT_FOUND));

        Restaurant restaurant = restaurantRepository.findByOrders(order).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!user.getOrders().contains(order))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_FEEDBACK);

        Feedback feedback = new Feedback();
        BeanUtils.copyProperties(feedbackDto, feedback);

        Feedback created = feedbackRepository.save(feedback);

        order.setFeedback(created);
        Order updated = orderRepository.save(order);

        Notification notification = new Notification();

        notification.setContent("Feedback received from " + user.getName());
        notification.setTime(LocalDateTime.now());
        notification.setOrder(updated);
        notification.setIsViewed(false);

        Notification saved = notificationRepository.save(notification);
        restaurant.getNotifications().add(saved);
        restaurantRepository.save(restaurant);

        return created;
    }

    public Feedback update(FeedbackDto feedbackDto) {
        Feedback old = feedbackRepository.findById(feedbackDto.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.FEEDBACK_NOT_FOUND));

        Order order = orderRepository.findByFeedback(old).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.ORDER_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!user.getOrders().contains(order))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_FEEDBACK);

        BeanUtils.copyProperties(feedbackDto, old);
        return feedbackRepository.save(old);
    }

    public void delete(String id) {
        Feedback feedback = feedbackRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.FEEDBACK_NOT_FOUND));

        Order order = orderRepository.findByFeedback(feedback).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.ORDER_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!user.getOrders().contains(order))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_FEEDBACK);

        List<Notification> notifications = notificationRepository.findAllByOrder(order);
        notificationRepository.deleteAll(notifications);

        order.setFeedback(null);
        orderRepository.save(order);

        feedbackRepository.deleteById(id);
    }
}
