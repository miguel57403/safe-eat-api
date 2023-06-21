package ipb.pt.safeeat.controller;

import ipb.pt.safeeat.service.CartService;
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
    public ResponseEntity<Object> findAll() {
        return ResponseEntity.ok(cartService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable String id) {
        return ResponseEntity.ok(cartService.findById(id));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Object> findByUser(@PathVariable String id) {
        return ResponseEntity.ok(cartService.findByUser(id));
    }

    @GetMapping("/{id}/isBuying")
    public ResponseEntity<Object> isBuying(@PathVariable String id) {
        return ResponseEntity.ok(cartService.isBuying(id));
    }

    @PutMapping
    public ResponseEntity<Object> empty(@RequestParam String cartId) {
        return ResponseEntity.ok().body(cartService.empty(cartId));
    }
}
