package ipb.pt.safeeat.controller;

import ipb.pt.safeeat.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<Object> findAll() {
        return ResponseEntity.ok(cartService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable String id) {
        return ResponseEntity.ok(cartService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestParam String userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.create(userId));
    }

    @PutMapping
    public ResponseEntity<Object> empty(@RequestParam String cartId) {
        return ResponseEntity.ok().body(cartService.reset(cartId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable String id) {
        cartService.delete(id);
        return ResponseEntity.ok().build();
    }
}
