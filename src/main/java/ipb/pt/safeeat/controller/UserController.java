package ipb.pt.safeeat.controller;

import ipb.pt.safeeat.dto.UserDto;
import ipb.pt.safeeat.model.User;
import ipb.pt.safeeat.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@CrossOrigin
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping
    @RolesAllowed("ADMIN")
    public ResponseEntity<Object> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable String id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping("/me")
    public ResponseEntity<Object> findMe() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(user);
    }

    @PutMapping
    public ResponseEntity<Object> update(@Valid @RequestBody UserDto userDto) {
        return ResponseEntity.ok().body(userService.update(userDto));
    }

    @PutMapping("/me/image")
    public ResponseEntity<Object> updateImage(@RequestParam("image") MultipartFile imageFile) throws IOException {
        return ResponseEntity.ok().body(userService.updateImage(imageFile));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable String id) {
        userService.delete(id);
        return ResponseEntity.ok().build();
    }
}
