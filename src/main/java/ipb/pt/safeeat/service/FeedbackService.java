package ipb.pt.safeeat.service;

import ipb.pt.safeeat.constants.FeedbackConstants;
import ipb.pt.safeeat.constants.OrderConstants;
import ipb.pt.safeeat.model.*;
import ipb.pt.safeeat.repository.FeedbackRepository;
import ipb.pt.safeeat.repository.OrderRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class FeedbackService {
    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private OrderRepository orderRepository;

    public List<Feedback> getAll() {
        return feedbackRepository.findAll();
    }

    public Feedback findById(String id) {
        return feedbackRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, FeedbackConstants.NOT_FOUND));
    }

    public Feedback create(Feedback feedback, String orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, OrderConstants.NOT_FOUND));

        Feedback created = feedbackRepository.save(feedback);

        order.setFeedback(created);
        orderRepository.save(order);

        return created;
    }

    public Feedback update(Feedback feedback) {
        Feedback old = feedbackRepository.findById(feedback.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, FeedbackConstants.NOT_FOUND));

        BeanUtils.copyProperties(feedback, old);
        return feedbackRepository.save(feedback);
    }

    public void delete(String id) {
        feedbackRepository.deleteById(id);
    }
}
