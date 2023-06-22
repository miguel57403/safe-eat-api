package ipb.pt.safeeat.controller;

import ipb.pt.safeeat.dto.ItemDto;
import ipb.pt.safeeat.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@CrossOrigin
@RequestMapping("/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return ResponseEntity.ok(itemService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable String id) {
        return ResponseEntity.ok(itemService.findById(id));
    }

    @GetMapping("/cart/{id}")
    public ResponseEntity<Object> findByCartId(@PathVariable String id) {
        return ResponseEntity.ok(itemService.findByCartId(id));
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemDto itemDto, @RequestParam String cartId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.create(itemDto, cartId));
    }

    @PostMapping("/many")
    public ResponseEntity<Object> createMany(@Valid @RequestBody List<ItemDto> itemDtos, @RequestParam String cartId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.createMany(itemDtos, cartId));
    }

    @PutMapping
    public ResponseEntity<Object> update(@Valid @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok().body(itemService.update(itemDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable String id, @RequestParam String cartId) {
        itemService.delete(id, cartId);
        return ResponseEntity.ok().build();
    }
}
