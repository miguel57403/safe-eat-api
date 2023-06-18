package ipb.pt.safeeat.controller;

import ipb.pt.safeeat.model.Ingredient;
import ipb.pt.safeeat.service.IngredientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@CrossOrigin
@RequestMapping("/ingredients")
public class IngredientController {

    @Autowired
    private IngredientService ingredientService;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return ResponseEntity.ok(ingredientService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable String id) {
        return ResponseEntity.ok(ingredientService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody Ingredient ingredient, @RequestParam String restaurantId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ingredientService.create(ingredient, restaurantId));
    }

    @PostMapping("/many")
    public ResponseEntity<Object> createMany(@Valid @RequestBody List<Ingredient> ingredients, @RequestParam String restaurantId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ingredientService.createMany(ingredients, restaurantId));
    }

    @PutMapping
    public ResponseEntity<Object> update(@Valid @RequestBody Ingredient ingredient) {
        return ResponseEntity.ok().body(ingredientService.update(ingredient));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable String id) {
        ingredientService.delete(id);
        return ResponseEntity.ok().build();
    }
}
