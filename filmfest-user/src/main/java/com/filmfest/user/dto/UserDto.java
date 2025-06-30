package com.filmfest.user.dto;

import com.filmfest.user.model.UserFilm;
import com.filmfest.user.model.enums.AppLanguage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private UUID uuid;
    private String username;
    private List<UserFilm> userFilms;
    private AppLanguage appLanguage;
}
