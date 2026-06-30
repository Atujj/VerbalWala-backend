package com.verbalwala.backend.service.impl;

import com.verbalwala.backend.dto.response.StudentProfileResponse;
import com.verbalwala.backend.entity.User;
import com.verbalwala.backend.service.StudentProfileService;
import com.verbalwala.backend.service.StudentSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentProfileServiceImpl
        implements StudentProfileService {

    private final StudentSecurityService studentSecurityService;

    @Override
    public StudentProfileResponse getProfile() {

        User student = studentSecurityService.getCurrentStudent();

        return StudentProfileResponse.builder()
                .name(student.getFullName())
                .email(student.getEmail())
                .build();

    }
}