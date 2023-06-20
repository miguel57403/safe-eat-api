package ipb.pt.safeeat.service;

import ipb.pt.safeeat.constant.FeedbackConstants;
import ipb.pt.safeeat.constant.OrderConstants;
import ipb.pt.safeeat.dto.FeedbackDto;
import ipb.pt.safeeat.model.Feedback;
import ipb.pt.safeeat.model.Order;
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

    public Feedback create(FeedbackDto feedbackDto, String orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, OrderConstants.NOT_FOUND));

        Feedback feedback = new Feedback();
        BeanUtils.copyProperties(feedbackDto, feedback);
        Feedback created = feedbackRepository.save(feedback);

        order.setFeedback(created);
        orderRepository.save(order);

        return created;
    }

    public Feedback update(FeedbackDto feedbackDto) {
        Feedback old = feedbackRepository.findById(feedbackDto.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, FeedbackConstants.NOT_FOUND));

        BeanUtils.copyProperties(feedbackDto, old);
        return feedbackRepository.save(old);
    }

    public void delete(String id) {
        feedbackRepository.deleteById(id);
    }
}
