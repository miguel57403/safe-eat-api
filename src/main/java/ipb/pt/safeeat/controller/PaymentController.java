package ipb.pt.safeeat.controller;

import ipb.pt.safeeat.dto.PaymentDto;
import ipb.pt.safeeat.service.PaymentService;
import jakarta.annotation.security.RolesAllowed;
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
    @RolesAllowed("ADMIN")
    public ResponseEntity<Object> findAll() {
        return ResponseEntity.ok(paymentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable String id) {
        return ResponseEntity.ok(paymentService.findById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Object> findAllByUser(@PathVariable String userId) {
        return ResponseEntity.ok(paymentService.findAllByUser(userId));
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody PaymentDto paymentDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.create(paymentDto));
    }

    @PostMapping("/many")
    public ResponseEntity<Object> createMany(@Valid @RequestBody List<PaymentDto> paymentDtos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.createMany(paymentDtos));
    }

    @PutMapping
    public ResponseEntity<Object> update(@Valid @RequestBody PaymentDto paymentDto) {
        return ResponseEntity.ok().body(paymentService.update(paymentDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable String id) {
        paymentService.delete(id);
        return ResponseEntity.ok().build();
    }
}
