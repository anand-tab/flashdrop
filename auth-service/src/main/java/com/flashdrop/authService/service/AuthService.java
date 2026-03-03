package com.flashdrop.authService.service;

import com.flashdrop.authService.client.UserServiceClient;
import com.flashdrop.authService.config.SecurityConfig;
import com.flashdrop.authService.dto.AuthLoginRequest;
import com.flashdrop.authService.dto.AuthRequest;
import com.flashdrop.authService.dto.UserRequest;
import com.flashdrop.authService.entity.Auth;
import com.flashdrop.authService.repository.AuthRepository;
import com.flashdrop.authService.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserServiceClient userServiceClient;
    @Autowired
    private JwtUtil jwtUtil;

    public String registerUser(AuthRequest authRequest) {
        if(authRepository.findByEmail(authRequest.getEmail()).isPresent()){
            throw new RuntimeException("Email already exists");
        }
        Auth authUser = Auth.builder()
                .email(authRequest.getEmail())
                .password(passwordEncoder.encode(authRequest.getPassword()))
                .role("USER")
                .build();

        Auth saveAuthUser = authRepository.save(authUser);
        UserRequest userRequest = UserRequest.builder()
                .email(authRequest.getEmail())
                .firstName(authRequest.getFirstName())
                .lastName(authRequest.getLastName())
                .phoneNumber(authRequest.getPhoneNumber())
                .address(authRequest.getAddress())
                .build();

        log.info("Now posting to user-service");
        UserRequest userRequest1 = userServiceClient.addUser(userRequest);
        log.info("User is created");
        return "User is created";
    }

    public String login(AuthLoginRequest authLoginRequest) {
        Auth user = authRepository.findByEmail(authLoginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(authLoginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        String token = jwtUtil.generateToken(
                user.getAuthId(),
                user.getEmail(),
                user.getRole()
        );
        return token;
    }
}
