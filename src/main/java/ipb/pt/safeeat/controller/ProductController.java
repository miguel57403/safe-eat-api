package ipb.pt.safeeat.controller;

import ipb.pt.safeeat.model.Product;
import ipb.pt.safeeat.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@CrossOrigin
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return ResponseEntity.ok(productService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable String id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody Product product, @RequestParam String restaurantId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(product, restaurantId));
    }

    @PostMapping("/many")
    public ResponseEntity<Object> createMany(@Valid @RequestBody List<Product> products, @RequestParam String restaurantId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createMany(products, restaurantId));
    }

    @PutMapping
    public ResponseEntity<Object> update(@Valid @RequestBody Product product) {
        return ResponseEntity.ok().body(productService.update(product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable String id) {
        productService.delete(id);
        return ResponseEntity.ok().build();
    }
}
