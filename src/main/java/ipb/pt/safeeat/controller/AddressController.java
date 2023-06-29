package ipb.pt.safeeat.controller;

import ipb.pt.safeeat.dto.AddressDto;
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
        return ResponseEntity.ok(addressService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable String id) {
        return ResponseEntity.ok(addressService.findById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Object> findAllByUser(@PathVariable String userId) {
        return ResponseEntity.ok(addressService.findAllByUser(userId));
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody AddressDto addressDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.create(addressDto));
    }

    @PostMapping("/many")
    public ResponseEntity<Object> createMany(@Valid @RequestBody List<AddressDto> addressDtos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.createMany(addressDtos));
    }

    @PutMapping
    public ResponseEntity<Object> update(@Valid @RequestBody AddressDto addressDto) {
        return ResponseEntity.ok().body(addressService.update(addressDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable String id) {
        addressService.delete(id);
        return ResponseEntity.ok().build();
    }
}
