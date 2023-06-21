package ipb.pt.safeeat.controller;

import ipb.pt.safeeat.dto.RestaurantDto;
import ipb.pt.safeeat.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@CrossOrigin
@RequestMapping("/restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return ResponseEntity.ok(restaurantService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable String id) {
        return ResponseEntity.ok(restaurantService.findById(id));
    }

    @GetMapping("/productCategory/{id}")
    public ResponseEntity<Object> findByCategory(@PathVariable String id) {
        return ResponseEntity.ok(restaurantService.findByProductCategory(id));
    }

    @GetMapping("/owner/{id}")
    public ResponseEntity<Object> findByOwner(@PathVariable String id) {
        return ResponseEntity.ok(restaurantService.findByOwner(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Object> findByName(@PathVariable String name) {
        return ResponseEntity.ok(restaurantService.findAllByName(name));
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody RestaurantDto restaurantDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantService.create(restaurantDto));
    }

    @PostMapping("/many")
    public ResponseEntity<Object> createMany(@Valid @RequestBody List<RestaurantDto> restaurantDtos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantService.createMany(restaurantDtos));
    }

    @PutMapping
    public ResponseEntity<Object> update(@Valid @RequestBody RestaurantDto restaurantDto) {
        return ResponseEntity.ok().body(restaurantService.update(restaurantDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable String id) {
        restaurantService.delete(id);
        return ResponseEntity.ok().build();
    }
}
