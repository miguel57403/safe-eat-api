package ipb.pt.safeeat.controller;

import ipb.pt.safeeat.dto.ProductDto;
import ipb.pt.safeeat.service.ProductService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@CrossOrigin
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    @RolesAllowed("ADMIN")
    public ResponseEntity<Object> findAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable String id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<Object> findAllByRestaurant(@PathVariable String restaurantId) {
        return ResponseEntity.ok(productService.findAllByRestaurant(restaurantId));
    }

    @GetMapping("/restaurant/{restaurantId}/name/{name}")
    public ResponseEntity<Object> findByAllRestaurantAndName(@PathVariable String restaurantId, @PathVariable String name) {
        return ResponseEntity.ok(productService.findAllByRestaurantAndName(restaurantId, name));
    }

    @PostMapping("/restaurant/{restaurantId}")
    public ResponseEntity<Object> create(@Valid @RequestBody ProductDto productDto, @PathVariable String restaurantId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(productDto, restaurantId));
    }

    @PutMapping
    public ResponseEntity<Object> update(@Valid @RequestBody ProductDto productDto) {
        return ResponseEntity.ok().body(productService.update(productDto));
    }

    @PutMapping("/{id}/image")
    public ResponseEntity<Object> updateImage(@PathVariable String id, @RequestParam("image") MultipartFile imageFile) throws IOException {
        return ResponseEntity.ok().body(productService.updateImage(id, imageFile));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable String id) {
        productService.delete(id);
        return ResponseEntity.ok().build();
    }
}
