package ipb.pt.safeeat.service;

import ipb.pt.safeeat.utility.NotFoundConstants;
import ipb.pt.safeeat.dto.PaymentDto;
import ipb.pt.safeeat.model.Payment;
import ipb.pt.safeeat.model.User;
import ipb.pt.safeeat.repository.PaymentRepository;
import ipb.pt.safeeat.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    UserRepository userRepository;

    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    public Payment findById(String id) {
        return paymentRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.PAYMENT_NOT_FOUND));
    }

    public List<Payment> findAllByUser(String id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.USER_NOT_FOUND));

        return user.getPayments();
    }

    public Payment create(PaymentDto paymentDto, String userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.PAYMENT_NOT_FOUND));

        Payment payment = new Payment();
        BeanUtils.copyProperties(paymentDto, payment);
        Payment created = paymentRepository.save(payment);

        user.getPayments().add(created);
        userRepository.save(user);

        return created;
    }

    @Transactional
    public List<Payment> createMany(List<PaymentDto> paymentDtos, String userId) {
        List<Payment> created = new ArrayList<>();
        for (PaymentDto paymentDto : paymentDtos) {
            created.add(create(paymentDto, userId));
        }

        return created;
    }

    public Payment update(PaymentDto paymentDto) {
        Payment old = paymentRepository.findById(paymentDto.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.PAYMENT_NOT_FOUND));

        BeanUtils.copyProperties(paymentDto, old);
        return paymentRepository.save(old);
    }

    public void delete(String id) {
        Payment payment = paymentRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.PAYMENT_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!user.getPayments().contains(payment))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.PAYMENT_NOT_FOUND);

        user.getPayments().remove(payment);
        userRepository.save(user);
        paymentRepository.deleteById(id);
    }
}
