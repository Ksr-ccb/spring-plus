package org.example.expert.domain.user.dto.response;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProfileRepsonse {
    private final Long id;
    private final String nickname;
    private final String email;
    private final String profileImg;


    public UserProfileRepsonse(Long id, String nickname, String email, String profileImg) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.profileImg = profileImg;
    }
}
