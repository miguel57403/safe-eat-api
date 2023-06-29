package ipb.pt.safeeat.service;

import ipb.pt.safeeat.dto.FeedbackDto;
import ipb.pt.safeeat.model.Feedback;
import ipb.pt.safeeat.model.Order;
import ipb.pt.safeeat.model.User;
import ipb.pt.safeeat.repository.FeedbackRepository;
import ipb.pt.safeeat.repository.OrderRepository;
import ipb.pt.safeeat.utility.NotFoundConstants;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class FeedbackService {
    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private OrderRepository orderRepository;

    public List<Feedback> findAll() {
        return feedbackRepository.findAll();
    }

    public Feedback findById(String id) {
        return feedbackRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.FEEDBACK_NOT_FOUND));
    }

    public Feedback create(FeedbackDto feedbackDto, String orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.ORDER_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!user.getOrders().contains(order))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.ORDER_NOT_FOUND);

        Feedback feedback = new Feedback();
        BeanUtils.copyProperties(feedbackDto, feedback);
        Feedback created = feedbackRepository.save(feedback);

        order.setFeedback(created);
        orderRepository.save(order);

        return created;
    }

    public Feedback update(FeedbackDto feedbackDto) {
        Feedback old = feedbackRepository.findById(feedbackDto.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.FEEDBACK_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Order order = orderRepository.findByFeedback(old).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.ORDER_NOT_FOUND));

        if (!user.getOrders().contains(order))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.ORDER_NOT_FOUND);

        BeanUtils.copyProperties(feedbackDto, old);
        return feedbackRepository.save(old);
    }

    public void delete(String id) {
        Feedback feedback = feedbackRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.FEEDBACK_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Order order = orderRepository.findByFeedback(feedback).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.ORDER_NOT_FOUND));

        if (!user.getOrders().contains(order))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.ORDER_NOT_FOUND);

        order.setFeedback(null);
        orderRepository.save(order);
        feedbackRepository.deleteById(id);
    }
}
