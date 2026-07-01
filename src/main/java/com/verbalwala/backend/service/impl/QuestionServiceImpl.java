package com.verbalwala.backend.service.impl;

import com.verbalwala.backend.dto.request.CreateQuestionRequest;
import com.verbalwala.backend.dto.response.ApiResponse;
import com.verbalwala.backend.entity.Assessment;
import com.verbalwala.backend.entity.Question;
import com.verbalwala.backend.entity.User;
import com.verbalwala.backend.enums.AssessmentStatus;
import com.verbalwala.backend.enums.QuestionType;
import com.verbalwala.backend.exception.AssessmentNotFoundException;
import com.verbalwala.backend.exception.InvalidQuestionException;
import com.verbalwala.backend.mapper.QuestionMapper;
import com.verbalwala.backend.repository.AssessmentAttemptRepository;
import com.verbalwala.backend.repository.AssessmentRepository;
import com.verbalwala.backend.repository.QuestionRepository;
import com.verbalwala.backend.service.AdminSecurityService;
import com.verbalwala.backend.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.security.access.AccessDeniedException;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final AssessmentRepository assessmentRepository;
    private final QuestionRepository questionRepository;

    private final AdminSecurityService adminSecurityService;


    private final AssessmentAttemptRepository assessmentAttemptRepository;

    @Override
    public ApiResponse<Void> addQuestion(
            String assessmentId,
            CreateQuestionRequest request) {

        validateAssessmentCanBeModified(assessmentId);

        if (!assessmentRepository.existsById(assessmentId)) {
            throw new AssessmentNotFoundException("Assessment not found");
        }

        if (request.getType() == QuestionType.FILL_BLANK &&
                (request.getExpectedAnswer() == null ||
                        request.getExpectedAnswer().isBlank())) {

            throw new InvalidQuestionException(
                    "Expected answer is required for Fill Blank questions");
        }

        Question question =
                QuestionMapper.toEntity(request, assessmentId);

        questionRepository.save(question);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Question added successfully")
                .build();
    }

    @Override
    public ApiResponse<Void> updateQuestion(
            String questionId,
            CreateQuestionRequest request) {


        Question question =
                questionRepository.findById(questionId)
                        .orElseThrow(() ->
                                new RuntimeException("Question not found"));

        validateAssessmentCanBeModified(
                question.getAssessmentId()
        );

        Assessment assessment =
                assessmentRepository.findById(
                                question.getAssessmentId())
                        .orElseThrow(() ->
                                new RuntimeException("Assessment not found"));

        User admin =
                adminSecurityService.getCurrentAdmin();

        if (!assessment.getCreatedById().equals(admin.getId())) {

            throw new AccessDeniedException(
                    "Access denied"
            );

        }

        question.setType(request.getType());

        question.setQuestionText(
                request.getQuestionText());

        question.setExpectedAnswer(
                request.getExpectedAnswer());

        question.setAlternativeAnswers(
                request.getAlternativeAnswers());

        question.setMarks(
                request.getMarks());

        question.setQuestionOrder(
                request.getQuestionOrder());

        questionRepository.save(question);



        return ApiResponse.<Void>builder()
                .success(true)
                .message("Question updated successfully")
                .data(null)
                .build();
    }

    //Delete Question
    @Override
    public ApiResponse<Void> deleteQuestion(String questionId) {

        Question question =
                questionRepository.findById(questionId)
                        .orElseThrow(() ->
                                new RuntimeException("Question not found"));

        validateAssessmentCanBeModified(
                question.getAssessmentId()
        );

        Assessment assessment =
                assessmentRepository.findById(
                                question.getAssessmentId())
                        .orElseThrow(() ->
                                new RuntimeException("Assessment not found"));

        User admin =
                adminSecurityService.getCurrentAdmin();

        if (!assessment.getCreatedById().equals(admin.getId())) {

            throw new AccessDeniedException(
                    "Access denied"
            );

        }

        questionRepository.delete(question);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Question deleted successfully")
                .data(null)
                .build();
    }

    private void validateAssessmentCanBeModified(String assessmentId) {

        Assessment assessment =
                assessmentRepository.findById(assessmentId)
                        .orElseThrow(() ->
                                new RuntimeException("Assessment not found"));

        if (assessmentAttemptRepository.existsByAssessmentId(assessmentId)) {

            throw new IllegalStateException(
                    "This assessment has already been attempted by students and cannot be modified."
            );

        }

        if (assessment.getStatus() == AssessmentStatus.PUBLISHED) {

            assessment.setStatus(AssessmentStatus.DRAFT);

            assessmentRepository.save(assessment);

        }

    }

}