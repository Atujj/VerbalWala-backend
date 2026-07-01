package com.verbalwala.backend.service.impl;

import com.verbalwala.backend.dto.response.ProfileResponse;
import com.verbalwala.backend.entity.User;
import com.verbalwala.backend.repository.UserRepository;
import com.verbalwala.backend.service.ProfileService;
import com.verbalwala.backend.service.StudentSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl
        implements ProfileService {

    private final UserRepository userRepository;

    private final StudentSecurityService studentSecurityService;

    @Override
    public ProfileResponse getProfile() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        return ProfileResponse.builder()
                .name(user.getFullName())
                .email(user.getEmail())
                .build();

    }
}