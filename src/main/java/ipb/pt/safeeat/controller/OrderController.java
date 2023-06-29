package ipb.pt.safeeat.controller;

import ipb.pt.safeeat.dto.OrderDto;
import ipb.pt.safeeat.service.OrderService;
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
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    @RolesAllowed("ADMIN")
    public ResponseEntity<Object> findAll() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable String id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Object> findAllByUser(@PathVariable String userId) {
        return ResponseEntity.ok(orderService.findAllByUser(userId));
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<Object> findAllByRestaurant(@PathVariable String restaurantId) {
        return ResponseEntity.ok(orderService.findAllByRestaurant(restaurantId));
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody OrderDto orderDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(orderDto));
    }

    @PostMapping("/many")
    public ResponseEntity<Object> createMany(@Valid @RequestBody List<OrderDto> orderDtos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createMany(orderDtos));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateStatus(@PathVariable String id, @RequestParam String status) {
        return ResponseEntity.ok().body(orderService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @RolesAllowed("ADMIN")
    public ResponseEntity<Object> delete(@PathVariable String id) {
        orderService.delete(id);
        return ResponseEntity.ok().build();
    }
}
