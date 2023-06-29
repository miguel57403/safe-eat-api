package ipb.pt.safeeat.controller;

import ipb.pt.safeeat.service.CartService;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@CrossOrigin
@RequestMapping("/carts")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    @RolesAllowed("ADMIN")
    public ResponseEntity<Object> findAll() {
        return ResponseEntity.ok(cartService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable String id) {
        return ResponseEntity.ok(cartService.findById(id));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Object> findByUser(@PathVariable String id) {
        return ResponseEntity.ok(cartService.findByUser(id));
    }

    @GetMapping("/isEmpty")
    public ResponseEntity<Object> isEmpty() {
        return ResponseEntity.ok(cartService.isEmpty());
    }

    @PutMapping
    public ResponseEntity<Object> empty() {
        return ResponseEntity.ok().body(cartService.empty());
    }
}