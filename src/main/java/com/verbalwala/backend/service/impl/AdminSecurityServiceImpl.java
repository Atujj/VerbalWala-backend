package com.verbalwala.backend.service.impl;

import com.verbalwala.backend.entity.User;
import com.verbalwala.backend.enums.Role;
import com.verbalwala.backend.repository.UserRepository;
import com.verbalwala.backend.service.AdminSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminSecurityServiceImpl
        implements AdminSecurityService {

    private final UserRepository userRepository;

    @Override
    public User getCurrentAdmin() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        User admin = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found"
                        ));

        if (admin.getRole() != Role.ADMIN) {

            throw new AccessDeniedException(
                    "Only admins can access this resource"
            );

        }

        return admin;

    }

}