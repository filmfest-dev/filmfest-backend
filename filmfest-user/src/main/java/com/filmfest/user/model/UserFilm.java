package com.filmfest.user.model;

import com.filmfest.user.model.enums.UserFilmStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class UserFilm {
    @Id
    private String id;
    
    @Builder.Default
    private UserFilmStatus userFilmStatus=UserFilmStatus.UNSEEN;
}
