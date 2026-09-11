package com.example.tournament.controller.rest;

import com.example.tournament.dto.request.RegisterRequest;
import com.example.tournament.dto.response.UserResponse;
import com.example.tournament.entity.User;
import com.example.tournament.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {

        User user = userService.register(request.getUsername(), request.getPassword(), request.getRole());

        UserResponse response = UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}