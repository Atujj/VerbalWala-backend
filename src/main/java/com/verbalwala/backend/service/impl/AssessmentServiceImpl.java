package com.verbalwala.backend.service.impl;

import com.verbalwala.backend.dto.request.CreateAssessmentRequest;
import com.verbalwala.backend.dto.response.ApiResponse;
import com.verbalwala.backend.dto.response.AssessmentStudentResponse;
import com.verbalwala.backend.entity.Assessment;
import com.verbalwala.backend.entity.AssessmentAttempt;
import com.verbalwala.backend.entity.Question;
import com.verbalwala.backend.enums.AssessmentStatus;
import com.verbalwala.backend.enums.QuestionType;
import com.verbalwala.backend.repository.AssessmentAttemptRepository;
import com.verbalwala.backend.repository.AssessmentRepository;
import com.verbalwala.backend.repository.QuestionRepository;
import com.verbalwala.backend.repository.UserRepository;
import com.verbalwala.backend.service.AdminSecurityService;
import com.verbalwala.backend.service.AssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.verbalwala.backend.entity.User;


import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssessmentServiceImpl implements AssessmentService {

    private final AssessmentRepository assessmentRepository;

    private final AdminSecurityService adminSecurityService;

    private final QuestionRepository questionRepository;

    private final AssessmentAttemptRepository assessmentAttemptRepository;

    private final UserRepository userRepository;




    @Override
    public ApiResponse<String> createAssessment(CreateAssessmentRequest request) {

        User admin = adminSecurityService.getCurrentAdmin();

        Assessment assessment = Assessment.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(AssessmentStatus.DRAFT)
                .maxAttempts(request.getMaxAttempts())
                .fillBlankTime(request.getFillBlankTime())
                .passageReadTime(request.getPassageReadTime())
                .passageWriteTime(request.getPassageWriteTime())
                .emailWritingTime(request.getEmailWritingTime())
                .createdById(admin.getId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())

                .build();

        assessment = assessmentRepository.save(assessment);

        return ApiResponse.<String>builder()
                .success(true)
                .message("Assessment created successfully")
                .data(assessment.getId())
                .build();
    }

    @Override
    public ApiResponse<Void> publishAssessment(String assessmentId) {

        long questionCount = questionRepository.countByAssessmentId(assessmentId);

        if (questionCount == 0) {
            throw new IllegalStateException(
                    "Cannot publish assessment without questions."
            );
        }

        Assessment assessment =
                assessmentRepository.findById(assessmentId)
                        .orElseThrow(() ->
                                new RuntimeException("Assessment not found"));

        User admin =
                adminSecurityService.getCurrentAdmin();

        if (!assessment.getCreatedById().equals(admin.getId())) {

            throw new AccessDeniedException(
                    "Access denied"
            );

        }

        long fillBlankCount =
                questionRepository
                        .countByAssessmentIdAndType(
                                assessmentId,
                                QuestionType.FILL_BLANK
                        );

        long passageCount =
                questionRepository
                        .countByAssessmentIdAndType(
                                assessmentId,
                                QuestionType.PASSAGE
                        );

        long emailCount =
                questionRepository
                        .countByAssessmentIdAndType(
                                assessmentId,
                                QuestionType.EMAIL
                        );

        if (fillBlankCount == 0 ||
                passageCount == 0 ||
                emailCount == 0) {

            throw new IllegalStateException(
                    "Assessment must contain at least one Fill Blank, one Passage and one Email question."
            );

        }

        assessment.setStatus(AssessmentStatus.PUBLISHED);

        assessment.setUpdatedAt(LocalDateTime.now());

        assessmentRepository.save(assessment);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Assessment published successfully")
                .data(null)
                .build();
    }

    @Override
    public List<AssessmentStudentResponse> getStudents(
            String assessmentId) {

        User admin =
                adminSecurityService.getCurrentAdmin();

        Assessment assessment =
                assessmentRepository.findById(assessmentId)
                        .orElseThrow(() ->
                                new RuntimeException("Assessment not found"));

        if (!assessment.getCreatedById().equals(admin.getId())) {

            throw new AccessDeniedException(
                    "Access denied"
            );

        }

        int totalMarks =
                questionRepository
                        .findByAssessmentIdOrderByQuestionOrder(
                                assessmentId
                        )
                        .stream()
                        .mapToInt(Question::getMarks)
                        .sum();

        List<AssessmentAttempt> attempts =
                assessmentAttemptRepository.findByAssessmentId(
                        assessmentId
                );

        return attempts.stream()

                .map(attempt -> {

                    User student =
                            userRepository.findById(
                                    attempt.getStudentId()
                            ).orElseThrow();

                    return AssessmentStudentResponse.builder()

                            .attemptId(attempt.getId())

                            .studentName(student.getFullName())

                            .score(attempt.getObtainedMarks())

                            .totalMarks(totalMarks)

                            .build();

                })

                .toList();

    }
}