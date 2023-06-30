package ipb.pt.safeeat.controller;

import ipb.pt.safeeat.dto.IngredientDto;
import ipb.pt.safeeat.service.IngredientService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@CrossOrigin
@RequestMapping("/ingredients")
public class IngredientController {

    @Autowired
    private IngredientService ingredientService;

    @GetMapping
    @RolesAllowed("ADMIN")
    public ResponseEntity<Object> findAll() {
        return ResponseEntity.ok(ingredientService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable String id) {
        return ResponseEntity.ok(ingredientService.findById(id));
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<Object> findByAllRestaurant(@PathVariable String restaurantId) {
        return ResponseEntity.ok(ingredientService.findAllByRestaurant(restaurantId));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Object> findByAllProduct(@PathVariable String productId) {
        return ResponseEntity.ok(ingredientService.findAllByProduct(productId));
    }

    @PostMapping("/restaurant/{restaurantId}")
    public ResponseEntity<Object> create(@Valid @RequestBody IngredientDto ingredientDto, @PathVariable String restaurantId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ingredientService.create(ingredientDto, restaurantId));
    }

    @PutMapping
    public ResponseEntity<Object> update(@Valid @RequestBody IngredientDto ingredientDto) {
        return ResponseEntity.ok().body(ingredientService.update(ingredientDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable String id) {
        ingredientService.delete(id);
        return ResponseEntity.ok().build();
    }
}
