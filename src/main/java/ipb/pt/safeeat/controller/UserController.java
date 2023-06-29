package ipb.pt.safeeat.controller;

import ipb.pt.safeeat.dto.UserDto;
import ipb.pt.safeeat.model.User;
import ipb.pt.safeeat.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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


    @PostMapping("/many")
    public ResponseEntity<Object> createMany(@Valid @RequestBody List<UserDto> userDtos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createMany(userDtos));
    }

    @PutMapping
    public ResponseEntity<Object> update(@Valid @RequestBody UserDto userDto) {
        return ResponseEntity.ok().body(userService.update(userDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable String id) {
        userService.delete(id);
        return ResponseEntity.ok().build();
    }
}
