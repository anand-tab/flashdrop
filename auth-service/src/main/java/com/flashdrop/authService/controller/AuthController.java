package com.flashdrop.authService.controller;

import com.flashdrop.authService.dto.AuthLoginRequest;
import com.flashdrop.authService.dto.AuthRequest;
import com.flashdrop.authService.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")
public class AuthController {


    @Autowired
    private AuthService authService;

    @PostMapping("/registerUser")
    public ResponseEntity<String> registerUser(@RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(authService.registerUser(authRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody AuthLoginRequest authLoginRequest) {
        Map<String, String> response = new HashMap<>();
        response.put("token", authService.login(authLoginRequest));
        return ResponseEntity.ok(response);
    }
}
