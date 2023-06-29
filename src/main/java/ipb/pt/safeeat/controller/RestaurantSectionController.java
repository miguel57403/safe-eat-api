package ipb.pt.safeeat.controller;

import ipb.pt.safeeat.dto.RestaurantSectionDto;
import ipb.pt.safeeat.service.RestaurantSectionService;
import jakarta.annotation.security.RolesAllowed;
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
        return ResponseEntity.ok(restaurantSectionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable String id) {
        return ResponseEntity.ok(restaurantSectionService.findById(id));
    }

    @PostMapping
    @RolesAllowed("ADMIN")
    public ResponseEntity<Object> create(@Valid @RequestBody RestaurantSectionDto restaurantSectionDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantSectionService.create(restaurantSectionDto));
    }

    @PostMapping("/many")
    @RolesAllowed("ADMIN")
    public ResponseEntity<Object> createMany(@Valid @RequestBody List<RestaurantSectionDto> restaurantSectionDtos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantSectionService.createMany(restaurantSectionDtos));
    }

    @PutMapping
    @RolesAllowed("ADMIN")
    public ResponseEntity<Object> update(@Valid @RequestBody RestaurantSectionDto restaurantSectionDto) {
        return ResponseEntity.ok().body(restaurantSectionService.update(restaurantSectionDto));
    }

    @DeleteMapping("/{id}")
    @RolesAllowed("ADMIN")
    public ResponseEntity<Object> delete(@PathVariable String id) {
        restaurantSectionService.delete(id);
        return ResponseEntity.ok().build();
    }
}
