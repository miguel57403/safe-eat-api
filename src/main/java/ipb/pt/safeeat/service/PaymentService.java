package ipb.pt.safeeat.service;

import ipb.pt.safeeat.constants.PaymentConstants;
import ipb.pt.safeeat.model.Payment;
import ipb.pt.safeeat.model.User;
import ipb.pt.safeeat.repository.PaymentRepository;
import ipb.pt.safeeat.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    UserRepository userRepository;

    public List<Payment> getAll() {
        return paymentRepository.findAll();
    }

    public Payment findById(String id) {
        return paymentRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, PaymentConstants.NOT_FOUND));
    }

    public Payment create(Payment payment, String userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, PaymentConstants.NOT_FOUND));

        Payment created = paymentRepository.save(payment);

        user.getPayments().add(created);
        userRepository.save(user);

        return created;
    }

    @Transactional
    public List<Payment> createMany(List<Payment> payments, String userId) {
        List<Payment> created = new ArrayList<>();
        for(Payment payment : payments) {
            created.add(create(payment, userId));
        }

        return created;
    }

    public Payment update(Payment payment) {
        Payment old = paymentRepository.findById(payment.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, PaymentConstants.NOT_FOUND));

        BeanUtils.copyProperties(payment, old);
        return paymentRepository.save(payment);
    }

    public void delete(String id) {
        paymentRepository.deleteById(id);
    }
}