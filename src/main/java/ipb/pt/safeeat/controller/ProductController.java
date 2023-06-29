package ipb.pt.safeeat.controller;

import ipb.pt.safeeat.dto.ProductDto;
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
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable String id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findByRestaurant(@PathVariable String id) {
        return ResponseEntity.ok(productService.findAllByRestaurant(id));
    }

    @GetMapping("/restaurant/{id}/name/{name}")
    public ResponseEntity<Object> findByRestaurantAndName(@PathVariable String id, @PathVariable String name) {
        return ResponseEntity.ok(productService.findAllByRestaurantAndName(id, name));
    }

    @PostMapping("/{restaurantId}")
    public ResponseEntity<Object> create(@Valid @RequestBody ProductDto productDto, @PathVariable String restaurantId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(productDto, restaurantId));
    }

    @PostMapping("/many/{restaurantId}")
    public ResponseEntity<Object> createMany(@Valid @RequestBody List<ProductDto> productDtos, @PathVariable String restaurantId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createMany(productDtos, restaurantId));
    }

    @PutMapping
    public ResponseEntity<Object> update(@Valid @RequestBody ProductDto productDto) {
        return ResponseEntity.ok().body(productService.update(productDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable String id) {
        productService.delete(id);
        return ResponseEntity.ok().build();
    }
}
