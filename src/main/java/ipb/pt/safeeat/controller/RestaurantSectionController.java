package ipb.pt.safeeat.controller;

import ipb.pt.safeeat.model.RestaurantSection;
import ipb.pt.safeeat.service.RestaurantSectionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@CrossOrigin
@RequestMapping("/restaurantSections")
public class RestaurantSectionController {

    @Autowired
    private RestaurantSectionService restaurantSectionService;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return ResponseEntity.ok(restaurantSectionService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable String id) {
        return ResponseEntity.ok(restaurantSectionService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody RestaurantSection restaurantSection) {
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantSectionService.create(restaurantSection));
    }

    @PostMapping("/many")
    public ResponseEntity<Object> createMany(@Valid @RequestBody List<RestaurantSection> restaurantSections) {
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantSectionService.createMany(restaurantSections));
    }

    @PutMapping
    public ResponseEntity<Object> update(@Valid @RequestBody RestaurantSection restaurantSection) {
        return ResponseEntity.ok().body(restaurantSectionService.update(restaurantSection));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable String id) {
        restaurantSectionService.delete(id);
        return ResponseEntity.ok().build();
    }
}
