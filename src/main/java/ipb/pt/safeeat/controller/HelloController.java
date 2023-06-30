package ipb.pt.safeeat.controller;

import ipb.pt.safeeat.service.AzureBlobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@CrossOrigin
@RequestMapping("/")
public class HelloController {
    @Autowired
    private AzureBlobService azureBlobService;

    @GetMapping
    public ResponseEntity<Object> hello() {
        return ResponseEntity.ok("Hello SafeEat!");
    }
}
