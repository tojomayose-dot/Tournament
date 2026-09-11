package com.example.tournament.service.impl;

import com.example.tournament.entity.Role;
import com.example.tournament.entity.User;
import com.example.tournament.exception.BusinessException;
import com.example.tournament.repository.UserRepository;
import com.example.tournament.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User register(String username, String rawPassword, Role role) {

        if (userRepository.existsByUsername(username)) {
            throw new BusinessException("Ce nom d'utilisateur est déjà pris.");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        user.setEnabled(true);

        return userRepository.save(user);
    }
}