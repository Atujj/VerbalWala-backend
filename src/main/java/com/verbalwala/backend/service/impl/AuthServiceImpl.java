package com.verbalwala.backend.service.impl;

import com.verbalwala.backend.dto.request.LoginRequest;
import com.verbalwala.backend.dto.request.SignupRequest;
import com.verbalwala.backend.dto.response.ApiResponse;
import com.verbalwala.backend.dto.response.LoginData;
import com.verbalwala.backend.dto.response.UserResponse;
import com.verbalwala.backend.entity.User;
import com.verbalwala.backend.enums.Role;
import com.verbalwala.backend.exception.EmailAlreadyExistsException;
import com.verbalwala.backend.security.JwtService;
import com.verbalwala.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.verbalwala.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;


    private User buildStudent(SignupRequest request) {
        return User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.STUDENT)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));
    }

    @Override
    public ApiResponse<Void> signup(SignupRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        User user = buildStudent(request);
        userRepository.save(user);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("User Registered Successfully")
                .data(null)
                .build();
    }

    @Override
    public ApiResponse<LoginData> login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = getUserByEmail(request.getEmail());

        String token = jwtService.generateToken(user.getEmail());

        LoginData loginData = LoginData.builder()
                .token(token)
                .user(
                        UserResponse.builder()
                                .id(user.getId())
                                .fullName(user.getFullName())
                                .email(user.getEmail())
                                .role(user.getRole().name())
                                .build()
                )
                .build();

        return ApiResponse.<LoginData>builder()
                .success(true)
                .message("Login Successful")
                .data(loginData)
                .build();
    }


}