package ipb.pt.safeeat.controller;

import ipb.pt.safeeat.model.Payment;
import ipb.pt.safeeat.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@CrossOrigin
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return ResponseEntity.ok(paymentService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable String id) {
        return ResponseEntity.ok(paymentService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody Payment payment, @RequestParam String userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.create(payment, userId));
    }

    @PostMapping("/many")
    public ResponseEntity<Object> createMany(@Valid @RequestBody List<Payment> payments, @RequestParam String userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.createMany(payments, userId));
    }

    @PutMapping
    public ResponseEntity<Object> update(@Valid @RequestBody Payment payment) {
        return ResponseEntity.ok().body(paymentService.update(payment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable String id) {
        paymentService.delete(id);
        return ResponseEntity.ok().build();
    }
}
