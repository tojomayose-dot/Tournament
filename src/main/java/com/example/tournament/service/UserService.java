package com.example.tournament.service;

import com.example.tournament.entity.User;

public interface UserService {
    User register(String username, String rawPassword, com.example.tournament.entity.Role role);
}