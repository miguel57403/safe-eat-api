package ipb.pt.safeeat.controller;

import ipb.pt.safeeat.dto.RestrictionDto;
import ipb.pt.safeeat.service.RestrictionService;
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
@RequestMapping("/restrictions")
public class RestrictionController {

    @Autowired
    private RestrictionService restrictionService;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return ResponseEntity.ok(restrictionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable String id) {
        return ResponseEntity.ok(restrictionService.findById(id));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Object> findByUser(@PathVariable String id) {
        return ResponseEntity.ok(restrictionService.findAllByUser(id));
    }

    @PostMapping
    @RolesAllowed("ADMIN")
    public ResponseEntity<Object> create(@Valid @RequestBody RestrictionDto restrictionDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(restrictionService.create(restrictionDto));
    }

    @PostMapping("/many")
    @RolesAllowed("ADMIN")
    public ResponseEntity<Object> createMany(@Valid @RequestBody List<RestrictionDto> restrictionDtos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(restrictionService.createMany(restrictionDtos));
    }

    @PutMapping
    @RolesAllowed("ADMIN")
    public ResponseEntity<Object> update(@Valid @RequestBody RestrictionDto restrictionDto) {
        return ResponseEntity.ok().body(restrictionService.update(restrictionDto));
    }

    @DeleteMapping("/{id}")
    @RolesAllowed("ADMIN")
    public ResponseEntity<Object> delete(@PathVariable String id) {
        restrictionService.delete(id);
        return ResponseEntity.ok().build();
    }
}
