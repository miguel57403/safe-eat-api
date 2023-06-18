package ipb.pt.safeeat.controller;

import ipb.pt.safeeat.model.Advertisement;
import ipb.pt.safeeat.service.AdvertisementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@CrossOrigin
@RequestMapping("/advertisements")
public class AdvertisementController {

    @Autowired
    private AdvertisementService advertisementService;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return ResponseEntity.ok(advertisementService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable String id) {
        return ResponseEntity.ok().body(advertisementService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody Advertisement advertisement) {
        return ResponseEntity.status(HttpStatus.CREATED).body(advertisementService.create(advertisement));
    }

    @PostMapping("/many")
    public ResponseEntity<Object> createMany(@Valid @RequestBody List<Advertisement> advertisements) {
        return ResponseEntity.status(HttpStatus.CREATED).body(advertisementService.createMany(advertisements));
    }

    @PutMapping
    public ResponseEntity<Object> update(@Valid @RequestBody Advertisement advertisement) {
        return ResponseEntity.ok().body(advertisementService.update(advertisement));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable String id) {
        advertisementService.delete(id);
        return ResponseEntity.ok().build();
    }
}
