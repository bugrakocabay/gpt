package com.example.server.controllers;

import com.example.server.dto.responses.LoginResponse;
import com.example.server.dto.requests.LoginRequest;
import com.example.server.dto.responses.RegisterResponse;
import com.example.server.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> saveUser(@RequestBody LoginRequest user) throws Exception {
        return new ResponseEntity<>(userService.saveUser(user), null, 201);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest user) throws Exception {
        return new ResponseEntity<>(userService.login(user), null, 200);
    }
}
