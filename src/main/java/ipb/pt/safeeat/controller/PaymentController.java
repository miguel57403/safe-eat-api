package ipb.pt.safeeat.controller;

import ipb.pt.safeeat.dto.PaymentDto;
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
    public ResponseEntity<Object> create(@Valid @RequestBody PaymentDto paymentDto, @RequestParam String userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.create(paymentDto, userId));
    }

    @PostMapping("/many")
    public ResponseEntity<Object> createMany(@Valid @RequestBody List<PaymentDto> paymentDtos, @RequestParam String userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.createMany(paymentDtos, userId));
    }

    @PutMapping
    public ResponseEntity<Object> update(@Valid @RequestBody PaymentDto paymentDto) {
        return ResponseEntity.ok().body(paymentService.update(paymentDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable String id, @RequestParam String userId) {
        paymentService.delete(id, userId);
        return ResponseEntity.ok().build();
    }
}
