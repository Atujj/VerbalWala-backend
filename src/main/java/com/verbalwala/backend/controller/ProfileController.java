package com.verbalwala.backend.controller;

import com.verbalwala.backend.dto.response.ApiResponse;
import com.verbalwala.backend.dto.response.ProfileResponse;
import com.verbalwala.backend.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfile() {

        return ResponseEntity.ok(
                ApiResponse.<ProfileResponse>builder()
                        .success(true)
                        .message("Profile fetched successfully")
                        .data(profileService.getProfile())
                        .build()
        );
    }
}
