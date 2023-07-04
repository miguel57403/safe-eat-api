package ipb.pt.safeeat.controller;

import ipb.pt.safeeat.dto.DeliveryDto;
import ipb.pt.safeeat.service.DeliveryService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@CrossOrigin
@RequestMapping("/deliveries")
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    @GetMapping
    @RolesAllowed("ADMIN")
    public ResponseEntity<Object> findAll() {
        return ResponseEntity.ok(deliveryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable String id) {
        return ResponseEntity.ok(deliveryService.findById(id));
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<Object> findByAllRestaurant(@PathVariable String restaurantId) {
        return ResponseEntity.ok(deliveryService.findAllByRestaurant(restaurantId));
    }

    @PostMapping("/restaurant/{restaurantId}")
    public ResponseEntity<Object> create(@Valid @RequestBody DeliveryDto deliveryDto, @PathVariable String restaurantId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(deliveryService.create(deliveryDto, restaurantId));
    }

    @PutMapping("select/{id}")
    public ResponseEntity<Object> select(@PathVariable String id) {
        return ResponseEntity.ok().body(deliveryService.select(id));
    }

    @PutMapping
    public ResponseEntity<Object> update(@Valid @RequestBody DeliveryDto deliveryDto) {
        return ResponseEntity.ok().body(deliveryService.update(deliveryDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable String id) {
        deliveryService.delete(id);
        return ResponseEntity.ok().build();
    }
}
