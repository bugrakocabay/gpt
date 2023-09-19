package com.example.server.controllers;

import com.example.server.dto.responses.LoginResponse;
import com.example.server.dto.requests.LoginRequest;
import com.example.server.dto.responses.RegisterResponse;
import com.example.server.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> saveUser(@RequestBody LoginRequest user) throws Exception {
        return new ResponseEntity<>(authService.saveUser(user), null, 201);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest user) throws Exception {
        return new ResponseEntity<>(authService.login(user), null, 200);
    }
}
