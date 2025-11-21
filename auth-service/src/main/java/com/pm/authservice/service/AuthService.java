package com.pm.authservice.service;


import com.pm.authservice.dto.LoginRequestDTO;
import com.pm.authservice.model.User;
import com.pm.authservice.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import org.aspectj.weaver.patterns.IToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;


    public AuthService(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public Optional<String> authenticate(LoginRequestDTO loginRequestDTO){
        Optional<User> userOpt = userService.findByEmail(loginRequestDTO.getEmail());

        if (userOpt.isPresent()) {
            User u = userOpt.get();

            System.out.println("Raw: " + loginRequestDTO.getPassword());
            System.out.println("Encoded: " + u.getPassword());
            System.out.println("Matches: " + passwordEncoder.matches(loginRequestDTO.getPassword(), u.getPassword()));
        }

        Optional<String> token = userOpt
                .filter(u -> passwordEncoder.matches(loginRequestDTO.getPassword(), u.getPassword()))
                .map(u -> jwtUtil.generateToken(u.getEmail(), u.getRole()));

        return token;
    }

    public boolean validateToken(String token){
        try {
            jwtUtil.validateToken(token);
            return true;
        }
        catch(JwtException e){
            return false;
        }
    }
}
