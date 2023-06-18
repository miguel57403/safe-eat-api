package ipb.pt.safeeat.controller;

import ipb.pt.safeeat.model.LoginBody;
import ipb.pt.safeeat.model.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@CrossOrigin
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private LoginService loginService;

    @PostMapping
    public ResponseEntity<Object> login(@RequestBody LoginBody loginBody) {
        return ResponseEntity.status(HttpStatus.CREATED).body(loginService.login(loginBody));
    }
}
