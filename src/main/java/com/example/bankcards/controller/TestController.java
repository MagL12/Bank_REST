package com.example.bankcards.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/user/test")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> userTest() {
        return ResponseEntity.ok("This is a USER endpoint");
    }

    @GetMapping("/admin/test")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminTest() {
        return ResponseEntity.ok("This is an ADMIN endpoint");
    }

    @GetMapping("/test/protected")
    public ResponseEntity<String> protectedTest() {
        return ResponseEntity.ok("This is a protected endpoint");
    }
}