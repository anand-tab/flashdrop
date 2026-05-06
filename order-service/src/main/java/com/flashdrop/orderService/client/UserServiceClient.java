package com.flashdrop.orderService.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="user-service" ,url = "http://localhost:3000")
public interface UserServiceClient {

    @GetMapping("/api/users/{email}")
    Boolean verifyUser(@PathVariable("email") String email);
}
