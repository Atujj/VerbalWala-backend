package com.verbalwala.backend.service.impl;

import com.verbalwala.backend.dto.response.AdminAssessmentCardResponse;
import com.verbalwala.backend.dto.response.ApiResponse;
import com.verbalwala.backend.entity.Assessment;
import com.verbalwala.backend.entity.User;
import com.verbalwala.backend.enums.QuestionType;
import com.verbalwala.backend.repository.AssessmentAttemptRepository;
import com.verbalwala.backend.repository.AssessmentRepository;
import com.verbalwala.backend.repository.QuestionRepository;
import com.verbalwala.backend.service.AdminAssessmentListService;
import com.verbalwala.backend.service.AdminSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminAssessmentListServiceImpl
        implements AdminAssessmentListService {

    private final AssessmentRepository assessmentRepository;

    private final QuestionRepository questionRepository;

    private final AssessmentAttemptRepository assessmentAttemptRepository;

    private final AdminSecurityService adminSecurityService;

    @Override
    public ApiResponse<List<AdminAssessmentCardResponse>> getAssessments() {

        User admin = adminSecurityService.getCurrentAdmin();

        List<AdminAssessmentCardResponse> response =

                assessmentRepository
                        .findByCreatedByIdOrderByCreatedAtDesc(
                                admin.getId()
                        )
                        .stream()
                        .map(assessment ->

                                AdminAssessmentCardResponse.builder()

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

                                        .fillBlankCount(
                                                (int) questionRepository
                                                        .countByAssessmentIdAndType(
                                                                assessment.getId(),
                                                                QuestionType.FILL_BLANK
                                                        )
                                        )

                                        .passageCount(
                                                (int) questionRepository
                                                        .countByAssessmentIdAndType(
                                                                assessment.getId(),
                                                                QuestionType.PASSAGE
                                                        )
                                        )

                                        .emailCount(
                                                (int) questionRepository
                                                        .countByAssessmentIdAndType(
                                                                assessment.getId(),
                                                                QuestionType.EMAIL
                                                        )
                                        )

                                        .studentCount(
                                                (int) assessmentAttemptRepository
                                                        .countDistinctStudentIdByAssessmentId(
                                                                assessment.getId()
                                                        )
                                        )

                                        .build()

                        )
                        .toList();

        return ApiResponse.<List<AdminAssessmentCardResponse>>builder()

                .success(true)

                .message("Assessments fetched successfully")

                .data(response)

                .build();

    }

}