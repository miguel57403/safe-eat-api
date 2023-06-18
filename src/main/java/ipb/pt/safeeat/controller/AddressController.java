package ipb.pt.safeeat.controller;

import ipb.pt.safeeat.model.Address;
import ipb.pt.safeeat.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@CrossOrigin
@RequestMapping("/addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return ResponseEntity.ok(addressService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable String id) {
        return ResponseEntity.ok(addressService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody Address address, @RequestParam String userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.create(address, userId));
    }

    @PostMapping("/many")
    public ResponseEntity<Object> createMany(@Valid @RequestBody List<Address> addresses, @RequestParam String userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.createMany(addresses, userId));
    }

    @PutMapping
    public ResponseEntity<Object> update(@Valid @RequestBody Address address) {
        return ResponseEntity.ok().body(addressService.update(address));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable String id) {
        addressService.delete(id);
        return ResponseEntity.ok().build();
    }
}