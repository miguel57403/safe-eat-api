package ipb.pt.safeeat.service;

import ipb.pt.safeeat.constant.ForbiddenConstant;
import ipb.pt.safeeat.constant.NotFoundConstant;
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
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private UserRepository userRepository;

    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    public Payment findById(String id) {
        Payment payment = paymentRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.PAYMENT_NOT_FOUND));

        if (!getAuthenticatedUser().isAdmin() && !payment.getUserId().equals(getAuthenticatedUser().getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_PAYMENT);

        return payment;
    }

    public List<Payment> findAllByUser(String id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.USER_NOT_FOUND));

        if (!getAuthenticatedUser().isAdmin() && !getAuthenticatedUser().equals(user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_PAYMENT);

        return paymentRepository.findAllByUserId(user.getId());
    }

    public List<Payment> findMe() {
        return findAllByUser(getAuthenticatedUser().getId());
    }

    public Payment create(PaymentDto paymentDto) {
        User user = getAuthenticatedUser();

        Payment payment = new Payment();
        BeanUtils.copyProperties(paymentDto, payment);
        payment.setUserId(user.getId());
        return paymentRepository.save(payment);
    }

    public Payment update(PaymentDto paymentDto) {
        Payment old = paymentRepository.findById(paymentDto.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.PAYMENT_NOT_FOUND));

        if (!old.getUserId().equals(getAuthenticatedUser().getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_PAYMENT);

        BeanUtils.copyProperties(paymentDto, old);
        return paymentRepository.save(old);
    }

    public Payment select(String id) {
        List<Payment> payment = paymentRepository.findAllByUserId(getAuthenticatedUser().getId());
        payment.forEach(address -> address.setIsSelected(address.getId().equals(id)));
        List<Payment> saved = paymentRepository.saveAll(payment);

        return saved.stream()
                .filter(address -> address.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void delete(String id) {
        Payment payment = paymentRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.PAYMENT_NOT_FOUND));

        User user = getAuthenticatedUser();

        if (!payment.getUserId().equals(user.getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_PAYMENT);

        paymentRepository.deleteById(id);
    }

    private User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
