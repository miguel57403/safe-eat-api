package ipb.pt.safeeat.controller;

import ipb.pt.safeeat.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@CrossOrigin
@RequestMapping("/homes")
public class HomeController {

    @Autowired
    private HomeService homeService;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return ResponseEntity.ok(homeService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable String id) {
        return ResponseEntity.ok(homeService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Object> create() {
        return ResponseEntity.status(HttpStatus.CREATED).body(homeService.create());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable String id) {
        homeService.delete(id);
        return ResponseEntity.ok().build();
    }
}
