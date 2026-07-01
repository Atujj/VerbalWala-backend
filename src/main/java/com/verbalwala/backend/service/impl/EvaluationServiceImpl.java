package com.verbalwala.backend.service.impl;

import com.verbalwala.backend.dto.ai.AiEvaluationResponse;
import com.verbalwala.backend.dto.response.AssessmentResultResponse;
import com.verbalwala.backend.dto.response.QuestionResultResponse;
import com.verbalwala.backend.entity.AssessmentAttempt;
import com.verbalwala.backend.entity.Question;
import com.verbalwala.backend.entity.StudentAnswer;
import com.verbalwala.backend.entity.User;
import com.verbalwala.backend.enums.AttemptStatus;
import com.verbalwala.backend.enums.EvaluationStatus;
import com.verbalwala.backend.repository.AssessmentAttemptRepository;
import com.verbalwala.backend.repository.QuestionRepository;
import com.verbalwala.backend.repository.StudentAnswerRepository;
import com.verbalwala.backend.repository.UserRepository;
import com.verbalwala.backend.service.EvaluationService;
import com.verbalwala.backend.service.GeminiService;
import com.verbalwala.backend.service.StudentSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvaluationServiceImpl implements EvaluationService {

    private final AssessmentAttemptRepository assessmentAttemptRepository;

    private final StudentAnswerRepository studentAnswerRepository;

    private final QuestionRepository questionRepository;

    private final GeminiService geminiService;

    private final StudentSecurityService studentSecurityService;



    @Override
    public void evaluateAttempt(String attemptId) {

        AssessmentAttempt attempt =
                assessmentAttemptRepository.findById(attemptId)
                        .orElseThrow(() ->
                                new RuntimeException("Attempt not found"));

        attempt.setStatus(AttemptStatus.EVALUATING);
        assessmentAttemptRepository.save(attempt);

        List<StudentAnswer> answers =
                studentAnswerRepository.findByAttemptId(attemptId);

        List<Question> questions =
                questionRepository.findByAssessmentIdOrderByQuestionOrder(
                        attempt.getAssessmentId());

        Map<String, Question> questionMap =
                questions.stream()
                        .collect(Collectors.toMap(
                                Question::getId,
                                q -> q
                        ));

        int totalMarks = 0;

        boolean evaluationFailed = false;

        for (StudentAnswer answer : answers) {

            Question question =
                    questionMap.get(answer.getQuestionId());

            if (question == null) {
                continue;
            }

            switch (question.getType()) {

                case PASSAGE -> {
                    try {
                        evaluatePassage(answer, question);
                    } catch (Exception ex) {
                        evaluationFailed = true;
                        answer.setEvaluationStatus(EvaluationStatus.PENDING);
                        studentAnswerRepository.save(answer);

                    }
                }

                case EMAIL -> {
                    try {
                        evaluateEmail(answer, question);
                    } catch (Exception ex) {
                        evaluationFailed = true;
                        answer.setEvaluationStatus(EvaluationStatus.PENDING);
                        studentAnswerRepository.save(answer);

                    }
                }

                default -> {
                    // Fill Blank already evaluated
                }
            }

            totalMarks += answer.getObtainedMarks();

        }

        attempt.setObtainedMarks(totalMarks);

        attempt.setStatus(
                evaluationFailed
                        ? AttemptStatus.EVALUATING
                        : AttemptStatus.COMPLETED
        );

        assessmentAttemptRepository.save(attempt);

    }

    private void evaluatePassage(

            StudentAnswer answer,
            Question question) {



        AiEvaluationResponse response =
                geminiService.evaluatePassage(
                        question.getQuestionText(),
                        answer.getAnswer()
                );

        answer.setObtainedMarks(
                response.getOverallScore());

        answer.setFeedback(
                response.getFeedback());

        answer.setEvaluationStatus(
                EvaluationStatus.AI_EVALUATED);

        studentAnswerRepository.save(answer);

    }

    private void evaluateEmail(
            StudentAnswer answer,
            Question question) {



        AiEvaluationResponse response =
                geminiService.evaluateEmail(
                        question.getQuestionText(),
                        answer.getAnswer()
                );

        answer.setObtainedMarks(
                response.getOverallScore());

        answer.setFeedback(
                response.getFeedback());

        answer.setEvaluationStatus(
                EvaluationStatus.AI_EVALUATED);

        studentAnswerRepository.save(answer);

    }

    @Async
    @Override
    public void evaluateAttemptAsync(String attemptId) {

        try {

            evaluateAttempt(attemptId);

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    @Override
    public AssessmentResultResponse getResult(String attemptId) {

        AssessmentAttempt attempt =
                studentSecurityService.getStudentAttempt(
                        attemptId
                );

        List<StudentAnswer> answers =
                studentAnswerRepository.findByAttemptId(attemptId);

        List<Question> questions =
                questionRepository.findByAssessmentIdOrderByQuestionOrder(
                        attempt.getAssessmentId());

        int totalMarks = questions.stream()
                .mapToInt(Question::getMarks)
                .sum();

        Map<String, Question> questionMap =
                questions.stream()
                        .collect(Collectors.toMap(
                                Question::getId,
                                q -> q
                        ));

        List<QuestionResultResponse> results =
                answers.stream()
                        .map(answer -> {
                            Question question =
                                    questionMap.get(answer.getQuestionId());

                            return QuestionResultResponse.builder()
                                    .questionId(question.getId())
                                    .questionType(question.getType().name())
                                    .questionText(question.getQuestionText())
                                    .score(answer.getObtainedMarks())
                                    .feedback(answer.getFeedback())
                                    .build();

                        })
                        .toList();

        double percentage = totalMarks == 0
                ? 0.0
                : (attempt.getObtainedMarks() * 100.0) / totalMarks;

        return AssessmentResultResponse.builder()
                .overallScore(attempt.getObtainedMarks())
                .totalMarks(totalMarks)
                .percentage(percentage)
                .results(results)
                .build();

    }

}