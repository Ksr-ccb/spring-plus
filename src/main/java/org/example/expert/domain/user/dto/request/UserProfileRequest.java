package org.example.expert.domain.user.dto.request;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class UserProfileRequest {
    private final MultipartFile imageFile;

    public UserProfileRequest(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }
}
