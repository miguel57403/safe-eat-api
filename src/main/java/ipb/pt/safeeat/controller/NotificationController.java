package ipb.pt.safeeat.controller;

import ipb.pt.safeeat.dto.NotificationDto;
import ipb.pt.safeeat.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@CrossOrigin
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return ResponseEntity.ok(notificationService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable String id) {
        return ResponseEntity.ok(notificationService.findById(id));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Object> findByUser(@PathVariable String id) {
        return ResponseEntity.ok(notificationService.findAllByUser(id));
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody NotificationDto notificationDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.create(notificationDto));
    }

    @PostMapping("/many")
    public ResponseEntity<Object> createMany(@Valid @RequestBody List<NotificationDto> notificationDtos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.createMany(notificationDtos));
    }

    @PutMapping
    public ResponseEntity<Object> update(@Valid @RequestBody NotificationDto notificationDto) {
        return ResponseEntity.ok().body(notificationService.update(notificationDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> view(@PathVariable String id) {
        return ResponseEntity.ok().body(notificationService.view(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable String id) {
        notificationService.delete(id);
        return ResponseEntity.ok().build();
    }
}
