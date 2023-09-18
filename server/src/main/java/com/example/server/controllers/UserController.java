package com.example.server.controllers;

import com.example.server.dto.responses.UserResponse;
import com.example.server.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<UserResponse> getUser(@PathVariable String id) throws Exception {
        return new ResponseEntity<>(userService.getUserById(id), null, 200);
    }
}
