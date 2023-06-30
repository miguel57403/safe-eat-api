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

    @GetMapping("/user/{userId}")
    public ResponseEntity<Object> findByUser(@PathVariable String userId) {
        return ResponseEntity.ok(cartService.findByUser(userId));
    }

    @GetMapping("/isMineEmpty")
    public ResponseEntity<Object> isMineEmpty() {
        return ResponseEntity.ok(cartService.isMineEmpty());
    }

    @PutMapping("/emptyMine")
    public ResponseEntity<Object> empty() {
        return ResponseEntity.ok().body(cartService.emptyMine());
    }
}