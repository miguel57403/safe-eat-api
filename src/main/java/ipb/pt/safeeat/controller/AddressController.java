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
        return ResponseEntity.ok(addressService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable String id) {
        return ResponseEntity.ok(addressService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody AddressDto addressDto, @RequestParam String userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.create(addressDto, userId));
    }

    @PostMapping("/many")
    public ResponseEntity<Object> createMany(@Valid @RequestBody List<AddressDto> addressDtos, @RequestParam String userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.createMany(addressDtos, userId));
    }

    @PutMapping
    public ResponseEntity<Object> update(@Valid @RequestBody AddressDto addressDto) {
        return ResponseEntity.ok().body(addressService.update(addressDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable String id, @RequestParam String userId) {
        addressService.delete(id, userId);
        return ResponseEntity.ok().build();
    }
}
