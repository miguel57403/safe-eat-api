package ipb.pt.safeeat.controller;

import ipb.pt.safeeat.model.Login;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@CrossOrigin
@RequestMapping("/login")
public class LoginController {
    @PostMapping
    public ResponseEntity<Object> login() {
        Login login = new Login();
        login.setStatus("success");
        login.setMessage("Login successful");
        login.setToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c");

        return ResponseEntity.status(HttpStatus.CREATED).body(login);
    }

}
