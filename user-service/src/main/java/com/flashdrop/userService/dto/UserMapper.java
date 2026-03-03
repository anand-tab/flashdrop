package com.flashdrop.userService.dto;

import com.flashdrop.userService.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toDto(UserRequest userRequest);

    UserResponse toEntity(User user);

    List<UserResponse> toEntity(List<User> users);
}
