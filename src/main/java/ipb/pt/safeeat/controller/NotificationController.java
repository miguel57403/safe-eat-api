package ipb.pt.safeeat.controller;

import ipb.pt.safeeat.model.Notification;
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
        return ResponseEntity.ok(notificationService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable String id) {
        return ResponseEntity.ok(notificationService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody Notification notification) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.create(notification));
    }

    @PostMapping("/many")
    public ResponseEntity<Object> createMany(@Valid @RequestBody List<Notification> notifications) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.createMany(notifications));
    }

    @PutMapping
    public ResponseEntity<Object> update(@Valid @RequestBody Notification notification) {
        return ResponseEntity.ok().body(notificationService.update(notification));
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