package com.tujusembilan.foodorder.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tujusembilan.foodorder.dto.request.LoginRequest;
import com.tujusembilan.foodorder.dto.request.RegisterRequest;
import com.tujusembilan.foodorder.dto.response.LoginResponse;
import com.tujusembilan.foodorder.dto.response.MessageResponse;
import com.tujusembilan.foodorder.services.UserService;

@RestController
@RequestMapping("/user-management")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/users/sign-up")
    public ResponseEntity<MessageResponse> register(@RequestBody RegisterRequest request) {
        return userService.registerUser(request);
    }

    @PostMapping("/users/sign-in")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return userService.loginUser(request);
    }
}
