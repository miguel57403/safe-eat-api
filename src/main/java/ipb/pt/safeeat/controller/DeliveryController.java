package ipb.pt.safeeat.controller;

import ipb.pt.safeeat.dto.DeliveryDto;
import ipb.pt.safeeat.service.DeliveryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@CrossOrigin
@RequestMapping("/deliveries")
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return ResponseEntity.ok(deliveryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable String id) {
        return ResponseEntity.ok(deliveryService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody DeliveryDto deliveryDto, @RequestParam String restaurantId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(deliveryService.create(deliveryDto, restaurantId));
    }

    @PostMapping("/many")
    public ResponseEntity<Object> createMany(@Valid @RequestBody List<DeliveryDto> deliveryDtos, @RequestParam String restaurantId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(deliveryService.createMany(deliveryDtos, restaurantId));
    }

    @PutMapping
    public ResponseEntity<Object> update(@Valid @RequestBody DeliveryDto deliveryDto) {
        return ResponseEntity.ok().body(deliveryService.update(deliveryDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable String id, @RequestParam String restaurantId) {
        deliveryService.delete(id, restaurantId);
        return ResponseEntity.ok().build();
    }
}
