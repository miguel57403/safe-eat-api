package ipb.pt.safeeat.controller;

import ipb.pt.safeeat.model.Feedback;
import ipb.pt.safeeat.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@CrossOrigin
@RequestMapping("/feedbacks")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return ResponseEntity.ok(feedbackService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable String id) {
        return ResponseEntity.ok(feedbackService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody Feedback feedback, @RequestParam String orderId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(feedbackService.create(feedback, orderId));
    }

    @PutMapping
    public ResponseEntity<Object> update(@Valid @RequestBody Feedback feedback) {
        return ResponseEntity.ok().body(feedbackService.update(feedback));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable String id) {
        feedbackService.delete(id);
        return ResponseEntity.ok().build();
    }
}
