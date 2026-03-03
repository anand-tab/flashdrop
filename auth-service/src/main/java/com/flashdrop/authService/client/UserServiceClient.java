package com.flashdrop.authService.client;

import com.flashdrop.authService.dto.UserRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="user-service" ,url = "http://localhost:3000")
public interface UserServiceClient {

    @PostMapping("/api/users/postUser")
    UserRequest addUser(@RequestBody UserRequest userRequest);
}
