package ipb.pt.safeeat.controller;

import ipb.pt.safeeat.dto.ProductSectionDto;
import ipb.pt.safeeat.service.ProductSectionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@CrossOrigin
@RequestMapping("/productSections")
public class ProductSectionController {

    @Autowired
    private ProductSectionService productSectionService;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return ResponseEntity.ok(productSectionService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable String id) {
        return ResponseEntity.ok(productSectionService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ProductSectionDto productSectionDto, @RequestParam String restaurantId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productSectionService.create(productSectionDto, restaurantId));
    }

    @PostMapping("/many")
    public ResponseEntity<Object> createMany(@Valid @RequestBody List<ProductSectionDto> productSectionDtos, @RequestParam String restaurantId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productSectionService.createMany(productSectionDtos, restaurantId));
    }

    @PutMapping
    public ResponseEntity<Object> update(@Valid @RequestBody ProductSectionDto productSectionDto) {
        return ResponseEntity.ok().body(productSectionService.update(productSectionDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable String id, @RequestParam String restaurantId) {
        productSectionService.delete(id, restaurantId);
        return ResponseEntity.ok().build();
    }
}
