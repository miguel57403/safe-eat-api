package ipb.pt.safeeat.controller;

import ipb.pt.safeeat.dto.AdvertisementDto;
import ipb.pt.safeeat.service.AdvertisementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@CrossOrigin
@RequestMapping("/advertisements")
public class AdvertisementController {

    @Autowired
    private AdvertisementService advertisementService;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return ResponseEntity.ok(advertisementService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable String id) {
        return ResponseEntity.ok().body(advertisementService.findById(id));
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<Object> findAllByRestaurant(@PathVariable String restaurantId) {
        return ResponseEntity.ok(advertisementService.findAllByRestaurant(restaurantId));
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody AdvertisementDto advertisementDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(advertisementService.create(advertisementDto));
    }

    @PutMapping
    public ResponseEntity<Object> update(@Valid @RequestBody AdvertisementDto advertisementDto) {
        return ResponseEntity.ok().body(advertisementService.update(advertisementDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable String id) {
        advertisementService.delete(id);
        return ResponseEntity.ok().build();
    }
}
