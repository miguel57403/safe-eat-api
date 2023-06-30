package ipb.pt.safeeat.controller;

import ipb.pt.safeeat.dto.RestaurantDto;
import ipb.pt.safeeat.service.RestaurantService;
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
@RequestMapping("/restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return ResponseEntity.ok(restaurantService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable String id) {
        return ResponseEntity.ok(restaurantService.findById(id));
    }

    @GetMapping("/productCategory/{categoryId}")
    public ResponseEntity<Object> findAllByCategory(@PathVariable String categoryId) {
        return ResponseEntity.ok(restaurantService.findAllByProductCategory(categoryId));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<Object> findAllByOwner(@PathVariable String ownerId) {
        return ResponseEntity.ok(restaurantService.findAllByOwner(ownerId));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Object> findAllByName(@PathVariable String name) {
        return ResponseEntity.ok(restaurantService.findAllByName(name));
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody RestaurantDto restaurantDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantService.create(restaurantDto));
    }

    @PutMapping
    public ResponseEntity<Object> update(@Valid @RequestBody RestaurantDto restaurantDto) {
        return ResponseEntity.ok().body(restaurantService.update(restaurantDto));
    }

    @PutMapping("/{id}/logo")
    public ResponseEntity<Object> updateLogo(@PathVariable String id, @RequestParam("image") MultipartFile imageFile) throws IOException {
        return ResponseEntity.ok().body(restaurantService.updateLogo(id, imageFile));
    }

    @PutMapping("/{id}/cover")
    public ResponseEntity<Object> updateCover(@PathVariable String id, @RequestParam("image") MultipartFile imageFile) throws IOException {
        return ResponseEntity.ok().body(restaurantService.updateCover(id, imageFile));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable String id) {
        restaurantService.delete(id);
        return ResponseEntity.ok().build();
    }
}
