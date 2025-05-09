package org.example.expert.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.annotation.Auth;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.request.UserProfileRequest;
import org.example.expert.domain.user.dto.response.UserProfileRepsonse;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.service.S3Service;
import org.example.expert.domain.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final S3Service s3Service;

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PutMapping
    public void changePassword(@Auth AuthUser authUser, @RequestBody UserChangePasswordRequest userChangePasswordRequest) {
        userService.changePassword(authUser.getId(), userChangePasswordRequest);
    }

    @PostMapping("/profileImg")
    public ResponseEntity<UserProfileRepsonse> uploadProfileImge(
            @Auth AuthUser authUser,
            @ModelAttribute UserProfileRequest profileRequest) throws IOException {
        return ResponseEntity.ok(s3Service.uploadProfileImg(authUser, profileRequest.getImageFile()));
    }

    @DeleteMapping("/profileImg")
    public ResponseEntity<UserProfileRepsonse> uploadProfileImge(
            @Auth AuthUser authUser) throws IOException {
        return ResponseEntity.ok(s3Service.delete(authUser));
    }

}
