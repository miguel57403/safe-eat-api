package ipb.pt.safeeat.service;

import ipb.pt.safeeat.constant.ForbiddenConstant;
import ipb.pt.safeeat.constant.NotFoundConstant;
import ipb.pt.safeeat.dto.FeedbackDto;
import ipb.pt.safeeat.model.Feedback;
import ipb.pt.safeeat.model.Notification;
import ipb.pt.safeeat.model.Order;
import ipb.pt.safeeat.model.User;
import ipb.pt.safeeat.repository.FeedbackRepository;
import ipb.pt.safeeat.repository.NotificationRepository;
import ipb.pt.safeeat.repository.OrderRepository;
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
    private NotificationService notificationService;

    public List<Feedback> findAll() {
        return feedbackRepository.findAll();
    }

    public Feedback findById(String id) {
        Feedback feedback = feedbackRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.FEEDBACK_NOT_FOUND));

        Order order = orderRepository.findByFeedbackId(feedback.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.ORDER_NOT_FOUND));

        User user = getAuthenticatedUser();

        if (!user.isAdmin() && !order.getRestaurant().getOwnerId().equals(user.getId()) && !order.getClient().equals(user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_FEEDBACK);

        return feedback;
    }

    public Feedback findByOrder(String orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.ORDER_NOT_FOUND));

        User user = getAuthenticatedUser();

        if (!user.isAdmin() && !order.getRestaurant().getOwnerId().equals(user.getId()) && !order.getClient().equals(user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_FEEDBACK);

        return feedbackRepository.findById(order.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.FEEDBACK_NOT_FOUND));
    }

    public Feedback create(FeedbackDto feedbackDto, String orderId) {
        Order old = orderRepository.findById(orderId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.ORDER_NOT_FOUND));

        if (!old.getClient().equals(getAuthenticatedUser()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_FEEDBACK);

        Feedback feedback = new Feedback();
        BeanUtils.copyProperties(feedbackDto, feedback);

        Feedback created = feedbackRepository.save(feedback);

        old.setFeedbackId(created.getId());
        Order updated = orderRepository.save(old);
        notificationService.notifyFeedbackCreated(old, updated);

        return created;
    }

    public Feedback update(FeedbackDto feedbackDto) {
        Feedback old = feedbackRepository.findById(feedbackDto.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.FEEDBACK_NOT_FOUND));

        Order order = orderRepository.findByFeedbackId(old.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.ORDER_NOT_FOUND));

        if (!order.getClient().equals(getAuthenticatedUser()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_FEEDBACK);

        BeanUtils.copyProperties(feedbackDto, old);
        return feedbackRepository.save(old);
    }

    public void delete(String id) {
        Feedback feedback = feedbackRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.FEEDBACK_NOT_FOUND));

        Order order = orderRepository.findByFeedbackId(feedback.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.ORDER_NOT_FOUND));

        if (!order.getClient().equals(getAuthenticatedUser()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_FEEDBACK);

        order.setFeedbackId(null);
        orderRepository.save(order);

        feedbackRepository.deleteById(id);
    }

    private User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
