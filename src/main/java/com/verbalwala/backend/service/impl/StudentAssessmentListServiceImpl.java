package com.verbalwala.backend.service.impl;

import com.verbalwala.backend.dto.response.ApiResponse;
import com.verbalwala.backend.dto.response.AttemptSummaryResponse;
import com.verbalwala.backend.dto.response.StudentAssessmentCardResponse;
import com.verbalwala.backend.entity.Assessment;
import com.verbalwala.backend.entity.AssessmentAttempt;
import com.verbalwala.backend.entity.User;
import com.verbalwala.backend.enums.AssessmentStatus;
import com.verbalwala.backend.enums.AttemptStatus;
import com.verbalwala.backend.repository.AssessmentAttemptRepository;
import com.verbalwala.backend.repository.AssessmentRepository;
import com.verbalwala.backend.repository.QuestionRepository;
import com.verbalwala.backend.service.StudentAssessmentListService;
import com.verbalwala.backend.service.StudentSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;





@Service
@RequiredArgsConstructor
public class StudentAssessmentListServiceImpl implements StudentAssessmentListService {

    private final AssessmentAttemptRepository assessmentAttemptRepository;

    private final StudentSecurityService studentSecurityService;

    private final AssessmentRepository assessmentRepository;

    private final QuestionRepository questionRepository;

    @Override
    public ApiResponse<List<StudentAssessmentCardResponse>> getAssessments() {

        User student = studentSecurityService.getCurrentStudent();

        List<StudentAssessmentCardResponse> response =
                assessmentRepository
                        .findByStatus(AssessmentStatus.PUBLISHED)
                        .stream()
                        .map(assessment -> {

                            List<AssessmentAttempt> attempts =
                                    assessmentAttemptRepository
                                            .findByAssessmentIdAndStudentIdOrderByAttemptNumberAsc(
                                                    assessment.getId(),
                                                    student.getId()
                                            );

                            List<AttemptSummaryResponse> attemptResponses =
                                    attempts.stream()
                                            .map(attempt ->

                                                    AttemptSummaryResponse.builder()

                                                            .attemptId(
                                                                    attempt.getId()
                                                            )

                                                            .attemptNumber(
                                                                    attempt.getAttemptNumber()
                                                            )

                                                            .status(
                                                                    attempt.getStatus()
                                                            )

                                                            .overallScore(
                                                                    attempt.getObtainedMarks()
                                                            )

                                                            .percentage(
                                                                    attempt.getObtainedMarks() == null ||
                                                                            attempt.getTotalMarks() == null ||
                                                                            attempt.getTotalMarks() == 0
                                                                            ? null
                                                                            : (attempt.getObtainedMarks() * 100.0)
                                                                            / attempt.getTotalMarks()
                                                            )

                                                            .submittedAt(
                                                                    attempt.getSubmittedAt()
                                                            )

                                                            .canViewResult(
                                                                    attempt.getStatus()
                                                                            == AttemptStatus.COMPLETED
                                                            )



                                                            .build()

                                            )
                                            .toList();

                            return StudentAssessmentCardResponse.builder()

                                    .assessmentId(
                                            assessment.getId()
                                    )

                                    .title(
                                            assessment.getTitle()
                                    )

                                    .description(
                                            assessment.getDescription()
                                    )

                                    .duration(
                                            (
                                                    assessment.getFillBlankTime()
                                                            + assessment.getPassageReadTime()
                                                            + assessment.getPassageWriteTime()
                                                            + assessment.getEmailWritingTime()
                                            ) / 60
                                    )

                                    .totalQuestions(
                                            (int) questionRepository.countByAssessmentId(
                                                    assessment.getId()
                                            )
                                    )

                                    .maxAttempts(
                                            assessment.getMaxAttempts()
                                    )

                                    .attemptsUsed(
                                            (int) attempts.stream()
                                                    .filter(a ->
                                                            a.getStatus() != AttemptStatus.STARTED
                                                    )
                                                    .count()

                                    )

                                    .latestStatus(
                                            attempts.isEmpty()
                                                    ? null
                                                    : attempts.get(attempts.size() - 1).getStatus()
                                    )

                                    .attempts(
                                            attemptResponses.stream()
                                                    .filter(a ->
                                                            a.getStatus()
                                                                    != AttemptStatus.STARTED
                                                    )
                                                    .toList()
                                    )

                                    .build();

                        })
                        .toList();

        return ApiResponse.<List<StudentAssessmentCardResponse>>builder()

                .success(true)

                .message("Assessments fetched successfully")

                .data(response)

                .build();

    }
}
