package com.example.server.user;

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
    public ResponseEntity<UserResponseDto> saveUser(@RequestBody UserDto user) throws Exception {
        return new ResponseEntity<>(userService.saveUser(user), null, 201);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody UserDto user) throws Exception {
        return new ResponseEntity<>(userService.login(user), null, 200);
    }
}
