package com.flashdrop.userService.service;

import com.flashdrop.userService.dto.UserMapper;
import com.flashdrop.userService.dto.UserRequest;
import com.flashdrop.userService.dto.UserResponse;
import com.flashdrop.userService.entity.User;
import com.flashdrop.userService.repository.UserRepository;
import jdk.jshell.spi.ExecutionControl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;


    public UserResponse addUser(UserRequest userRequest) {

        if(userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            log.error("Validation failed: Email {} already exists", userRequest.getEmail());
            throw new RuntimeException("Email already exists" + userRequest.getEmail());
        }
       log.info("UserService addUser");
        User user = new User();
        user = userMapper.toDto(userRequest);
        log.info("Sending data to database");
        userRepository.save(user);
        log.info("User saved");
        return userMapper.toEntity(user);

    }

    public List<UserResponse> listUser() {
        log.info("UserService listUser");
        List<User> users = userRepository.findAll();
        log.info("Users found");
        List<UserResponse> userResponses = userMapper.toEntity(users);
        return userResponses;
    }

    public String deleteUser(String userId) {
        log.info("UserService deleteUser");
        userRepository.deleteById(userId);
        return "User deleted";
    }
}
