package com.flashdrop.userService.controller;

import com.flashdrop.userService.dto.UserRequest;
import com.flashdrop.userService.dto.UserResponse;
import com.flashdrop.userService.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping("/postUser")
    public ResponseEntity<UserResponse> addUser(@RequestBody UserRequest userRequest){
        return ResponseEntity.ok(userService.addUser(userRequest));
    }

    @GetMapping("/getUsers")
    public ResponseEntity<List<UserResponse>> listUsers(){
        return ResponseEntity.ok(userService.listUser());
    }

    @DeleteMapping("/deleteUser/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable("userId") String userId) {
        return ResponseEntity.ok(userService.deleteUser(userId));
    }
}
