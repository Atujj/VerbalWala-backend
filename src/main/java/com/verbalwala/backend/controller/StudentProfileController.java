package com.verbalwala.backend.controller;

import com.verbalwala.backend.dto.response.ApiResponse;
import com.verbalwala.backend.dto.response.StudentProfileResponse;
import com.verbalwala.backend.service.StudentProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentProfileController {

    private final StudentProfileService studentProfileService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<StudentProfileResponse>> getProfile() {

        return ResponseEntity.ok(
                ApiResponse.<StudentProfileResponse>builder()
                        .success(true)
                        .message("Profile fetched successfully")
                        .data(studentProfileService.getProfile())
                        .build()
        );
    }
}
