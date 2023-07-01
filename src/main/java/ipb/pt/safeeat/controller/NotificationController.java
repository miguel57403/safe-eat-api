package ipb.pt.safeeat.controller;

import ipb.pt.safeeat.service.NotificationService;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@CrossOrigin
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    @RolesAllowed("ADMIN")
    public ResponseEntity<Object> findAll() {
        return ResponseEntity.ok(notificationService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable String id) {
        return ResponseEntity.ok(notificationService.findById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Object> findAllByUser(@PathVariable String userId) {
        return ResponseEntity.ok(notificationService.findAllByUser(userId));
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
