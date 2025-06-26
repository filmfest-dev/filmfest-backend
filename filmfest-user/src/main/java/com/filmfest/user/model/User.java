package com.filmfest.user.model;

import com.filmfest.user.model.enums.AppLanguage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
@Document(collection="users")
public class User {
    
    @Id
    private UUID uuid;
    
    @Indexed(unique = true)
    private String username;
    
    private List<UserFilm> userFilms;
    private AppLanguage appLanguage;
}
