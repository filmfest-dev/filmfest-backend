package com.filmfest.user.mapper;

import com.filmfest.user.dto.UserDto;
import com.filmfest.user.model.User;

public class UserMapper {
    public UserMapper() {
    }
    
    public static User toEntity(UserDto dto) {
        return User.builder()
                .uuid(dto.getUuid())
                .username(dto.getUsername())
                .userFilms(dto.getUserFilms())
                .appLanguage(dto.getAppLanguage())
                .build();
    }
    
    public static UserDto toDto(User user) {
        return new UserDto(
                user.getUuid(),
                user.getUsername(),
                user.getUserFilms(),
                user.getAppLanguage()
        );
    }
}
