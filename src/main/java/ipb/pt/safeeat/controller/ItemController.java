package ipb.pt.safeeat.controller;

import ipb.pt.safeeat.dto.ItemDto;
import ipb.pt.safeeat.service.ItemService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@CrossOrigin
@RequestMapping("/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping
    @RolesAllowed("ADMIN")
    public ResponseEntity<Object> findAll() {
        return ResponseEntity.ok(itemService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable String id) {
        return ResponseEntity.ok(itemService.findById(id));
    }

    @GetMapping("/cart/{cartId}")
    public ResponseEntity<Object> findAllByCartId(@PathVariable String cartId) {
        return ResponseEntity.ok(itemService.findAllByCartId(cartId));
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemDto itemDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.create(itemDto));
    }

    @PutMapping
    public ResponseEntity<Object> update(@Valid @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok().body(itemService.update(itemDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable String id) {
        itemService.delete(id);
        return ResponseEntity.ok().build();
    }
}
