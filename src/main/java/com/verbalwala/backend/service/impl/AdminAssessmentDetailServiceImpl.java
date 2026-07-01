package com.verbalwala.backend.service.impl;

import com.verbalwala.backend.dto.response.AdminAssessmentDetailResponse;
import com.verbalwala.backend.dto.response.AdminQuestionResponse;
import com.verbalwala.backend.dto.response.ApiResponse;
import com.verbalwala.backend.dto.response.QuestionResponse;
import com.verbalwala.backend.entity.Assessment;
import com.verbalwala.backend.entity.Question;
import com.verbalwala.backend.entity.User;
import com.verbalwala.backend.repository.AssessmentRepository;
import com.verbalwala.backend.repository.QuestionRepository;
import com.verbalwala.backend.service.AdminAssessmentDetailService;
import com.verbalwala.backend.service.AdminSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminAssessmentDetailServiceImpl
        implements AdminAssessmentDetailService {

    private final AssessmentRepository assessmentRepository;

    private final QuestionRepository questionRepository;

    private final AdminSecurityService adminSecurityService;

    @Override
    public ApiResponse<AdminAssessmentDetailResponse> getAssessment(
            String assessmentId
    ) {

        User admin =
                adminSecurityService.getCurrentAdmin();

        Assessment assessment =
                assessmentRepository.findById(assessmentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Assessment not found"
                                ));

        if (!assessment.getCreatedById().equals(admin.getId())) {

            throw new AccessDeniedException(
                    "Access denied"
            );

        }

        List<AdminQuestionResponse> questions =
                questionRepository
                        .findByAssessmentIdOrderByQuestionOrder(
                                assessmentId
                        )
                        .stream()
                        .map(question ->

                                AdminQuestionResponse.builder()

                                        .id(question.getId())

                                        .type(question.getType())

                                        .questionText(question.getQuestionText())

                                        .expectedAnswer(question.getExpectedAnswer())

                                        .alternativeAnswers(question.getAlternativeAnswers())

                                        .marks(question.getMarks())

                                        .questionOrder(question.getQuestionOrder())

                                        .build()

                        )
                        .toList();

        AdminAssessmentDetailResponse response =
                AdminAssessmentDetailResponse.builder()

                        .assessmentId(
                                assessment.getId()
                        )

                        .title(
                                assessment.getTitle()
                        )

                        .description(
                                assessment.getDescription()
                        )

                        .status(
                                assessment.getStatus()
                        )

                        .maxAttempts(
                                assessment.getMaxAttempts()
                        )

                        .fillBlankTime(
                                assessment.getFillBlankTime()
                        )

                        .passageReadTime(
                                assessment.getPassageReadTime()
                        )

                        .passageWriteTime(
                                assessment.getPassageWriteTime()
                        )

                        .emailWritingTime(
                                assessment.getEmailWritingTime()
                        )

                        .questions(
                                questions
                        )

                        .build();

        return ApiResponse.<AdminAssessmentDetailResponse>builder()

                .success(true)

                .message("Assessment fetched successfully")

                .data(response)

                .build();

    }

}