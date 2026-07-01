package com.verbalwala.backend.service.impl;

import com.verbalwala.backend.entity.AssessmentAttempt;
import com.verbalwala.backend.entity.User;
import com.verbalwala.backend.exception.AttemptNotFoundException;
import com.verbalwala.backend.repository.AssessmentAttemptRepository;
import com.verbalwala.backend.repository.UserRepository;
import com.verbalwala.backend.service.StudentSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentSecurityServiceImpl
        implements StudentSecurityService {

    private final UserRepository userRepository;

    private final AssessmentAttemptRepository assessmentAttemptRepository;

    @Override
    public User getCurrentStudent() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

    }

    @Override
    public AssessmentAttempt getStudentAttempt(String attemptId) {


        User student = getCurrentStudent();

        AssessmentAttempt attempt =
                assessmentAttemptRepository.findById(attemptId)
                        .orElseThrow(() ->
                                new AttemptNotFoundException(
                                        "Attempt not found"
                                ));

        if (!attempt.getStudentId().equals(student.getId())) {

            throw new AccessDeniedException(
                    "You are not allowed to access this attempt"
            );

        }




        return attempt;
    }
}