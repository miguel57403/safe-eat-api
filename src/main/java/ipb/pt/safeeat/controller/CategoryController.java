package ipb.pt.safeeat.controller;

import ipb.pt.safeeat.dto.CategoryDto;
import ipb.pt.safeeat.service.CategoryService;
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
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable String id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @PostMapping
    @RolesAllowed("ADMIN")
    public ResponseEntity<Object> create(@Valid @RequestBody CategoryDto categoryDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(categoryDto));
    }

    @PostMapping("/many")
    @RolesAllowed("ADMIN")
    public ResponseEntity<Object> createMany(@Valid @RequestBody List<CategoryDto> categoryDtos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createMany(categoryDtos));
    }

    @PutMapping
    @RolesAllowed("ADMIN")
    public ResponseEntity<Object> update(@Valid @RequestBody CategoryDto categoryDto) {
        return ResponseEntity.ok().body(categoryService.update(categoryDto));
    }

    @DeleteMapping("/{id}")
    @RolesAllowed("ADMIN")
    public ResponseEntity<Object> delete(@PathVariable String id) {
        categoryService.delete(id);
        return ResponseEntity.ok().build();
    }
}
