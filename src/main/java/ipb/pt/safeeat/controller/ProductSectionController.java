package ipb.pt.safeeat.controller;

import ipb.pt.safeeat.dto.ProductSectionDto;
import ipb.pt.safeeat.service.ProductSectionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@CrossOrigin
@RequestMapping("/productSections")
public class ProductSectionController {

    @Autowired
    private ProductSectionService productSectionService;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return ResponseEntity.ok(productSectionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable String id) {
        return ResponseEntity.ok(productSectionService.findById(id));
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<Object> findAllByRestaurant(@PathVariable String restaurantId) {
        return ResponseEntity.ok(productSectionService.findAllByRestaurant(restaurantId));
    }

    @PostMapping("/restaurant/{restaurantId}")
    public ResponseEntity<Object> create(@Valid @RequestBody ProductSectionDto productSectionDto, @PathVariable String restaurantId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productSectionService.create(productSectionDto, restaurantId));
    }

    @PutMapping
    public ResponseEntity<Object> update(@Valid @RequestBody ProductSectionDto productSectionDto) {
        return ResponseEntity.ok().body(productSectionService.update(productSectionDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable String id) {
        productSectionService.delete(id);
        return ResponseEntity.ok().build();
    }
}
