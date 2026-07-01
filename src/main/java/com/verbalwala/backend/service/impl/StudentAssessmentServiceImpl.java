package com.verbalwala.backend.service.impl;

import com.verbalwala.backend.dto.request.*;
import com.verbalwala.backend.dto.response.*;
import com.verbalwala.backend.entity.*;
import com.verbalwala.backend.enums.*;
import com.verbalwala.backend.exception.AssessmentNotFoundException;
import com.verbalwala.backend.exception.InvalidAssessmentException;
import com.verbalwala.backend.mapper.QuestionMapper;
import com.verbalwala.backend.repository.*;
import com.verbalwala.backend.service.EvaluationService;
import com.verbalwala.backend.service.StudentAssessmentService;
import com.verbalwala.backend.service.StudentSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import com.verbalwala.backend.entity.Assessment;
import com.verbalwala.backend.entity.User;
import com.verbalwala.backend.enums.AssessmentStatus;
import com.verbalwala.backend.enums.QuestionType;
import com.verbalwala.backend.exception.AssessmentNotFoundException;
import com.verbalwala.backend.exception.InvalidAssessmentException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.security.cert.Extension;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentAssessmentServiceImpl implements StudentAssessmentService {

    private final AssessmentRepository assessmentRepository;

    private final QuestionRepository questionRepository;

    private final AssessmentAttemptRepository assessmentAttemptRepository;

    private final UserRepository userRepository;

    private final StudentAnswerRepository studentAnswerRepository;

    private final EvaluationService evaluationService;

    private final StudentSecurityService studentSecurityService;

    @Override
    public ApiResponse<StartAssessmentResponse> startAssessment(String assessmentId) {

        User student = studentSecurityService.getCurrentStudent();

        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() ->
                        new AssessmentNotFoundException("Assessment not found"));

        if (assessment.getStatus() != AssessmentStatus.PUBLISHED) {
            throw new InvalidAssessmentException(
                    "Assessment is not available");
        }

        Optional<AssessmentAttempt> startedAttempt =
                assessmentAttemptRepository
                        .findByAssessmentIdAndStudentIdAndStatus(
                                assessmentId,
                                student.getId(),
                                AttemptStatus.STARTED
                        );

        AssessmentAttempt attempt;

        if (startedAttempt.isPresent()) {

            attempt = startedAttempt.get();

            studentAnswerRepository.deleteByAttemptId(
                    attempt.getId()
            );

            attempt.setObtainedMarks(0);
            attempt.setTotalMarks(null);
            attempt.setStartedAt(LocalDateTime.now());
            attempt.setSubmittedAt(null);
            attempt.setEndReason(null);

            assessmentAttemptRepository.save(attempt);

        } else {

            long attemptsUsed =
                    assessmentAttemptRepository
                            .countByAssessmentIdAndStudentIdAndStatusIn(
                                    assessmentId,
                                    student.getId(),
                                    List.of(
                                            AttemptStatus.COMPLETED,
                                            AttemptStatus.TERMINATED,
                                            AttemptStatus.SUBMITTED,
                                            AttemptStatus.EVALUATING
                                    )
                            );

            if (attemptsUsed >= assessment.getMaxAttempts()) {

                throw new RuntimeException(
                        "Maximum attempts exceeded"
                );

            }

            attempt =
                    AssessmentAttempt.builder()

                            .assessmentId(
                                    assessmentId
                            )

                            .studentId(
                                    student.getId()
                            )

                            .attemptNumber(
                                    (int) attemptsUsed + 1
                            )

                            .status(
                                    AttemptStatus.STARTED
                            )

                            .startedAt(
                                    LocalDateTime.now()
                            )

                            .build();

            assessmentAttemptRepository.save(
                    attempt
            );

        }

        List<Question> fillBlankQuestions =
                questionRepository.findByAssessmentIdAndTypeOrderByQuestionOrder(
                        assessmentId,
                        QuestionType.FILL_BLANK
                );

        List<QuestionResponse> questionResponses =
                fillBlankQuestions.stream()
                        .map(QuestionMapper::toResponse)
                        .toList();

        StartAssessmentResponse response =
                StartAssessmentResponse.builder()

                        .attemptId(
                                attempt.getId()
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

                        .fillBlankQuestions(
                                questionResponses
                        )

                        .build();

        return ApiResponse.<StartAssessmentResponse>builder()
                .success(true)
                .message("Assessment started successfully")
                .data(response)
                .build();
    }

    @Override
    public ApiResponse<PassageResponse> submitFillBlanks(
            String attemptId,
            SubmitFillBlankRequest request) {


        AssessmentAttempt attempt =
                studentSecurityService.getStudentAttempt(
                        attemptId
                );

        if (attempt.getStatus() != AttemptStatus.STARTED) {
            throw new RuntimeException("Assessment already submitted");
        }

        List<Question> questions =
                questionRepository.findByAssessmentIdAndTypeOrderByQuestionOrder(
                        attempt.getAssessmentId(),
                        QuestionType.FILL_BLANK
                );

        Map<String, Question> questionMap =
                questions.stream()
                        .collect(Collectors.toMap(
                                Question::getId,
                                q -> q
                        ));

        Integer obtainedMarks = 0;

        List<StudentAnswer> studentAnswers = new ArrayList<>();

        for (FillBlankAnswerRequest answerRequest : request.getAnswers()) {

            Question question =
                    questionMap.get(answerRequest.getQuestionId());

            if (question == null) {
                continue;
            }

            boolean correct = isCorrectAnswer(
                    answerRequest.getAnswer(),
                    question
            );

            int marks = correct
                    ? question.getMarks()
                    : 0;

            obtainedMarks += marks;

            StudentAnswer studentAnswer =
                    StudentAnswer.builder()
                            .attemptId(attemptId)
                            .questionId(question.getId())
                            .answer(answerRequest.getAnswer())
                            .obtainedMarks(marks)
                            .evaluationStatus(
                                    EvaluationStatus.AUTO_EVALUATED
                            )
                            .feedback(
                                    correct
                                            ? List.of("Correct answer.")
                                            : List.of("Incorrect answer.")
                            )
                            .build();

            studentAnswers.add(studentAnswer);

        }
        studentAnswerRepository.saveAll(studentAnswers);

        attempt.setObtainedMarks(obtainedMarks);
        assessmentAttemptRepository.save(attempt);

        List<QuestionResponse> passages =
                questionRepository
                        .findByAssessmentIdAndTypeOrderByQuestionOrder(
                                attempt.getAssessmentId(),
                                QuestionType.PASSAGE
                        )
                        .stream()
                        .map(QuestionMapper::toResponse)
                        .toList();

        Assessment assessment =
                assessmentRepository.findById(
                                attempt.getAssessmentId())
                        .orElseThrow(() ->
                                new AssessmentNotFoundException(
                                        "Assessment not found"));

        PassageResponse response =
                PassageResponse.builder()

                        .readingTime(
                                assessment.getPassageReadTime()
                        )

                        .writingTime(
                                assessment.getPassageWriteTime()
                        )

                        .passageQuestions(
                                passages
                        )

                        .build();



        return ApiResponse.<PassageResponse>builder()
                .success(true)
                .message("Fill Blank section submitted")
                .data(response)
                .build();

    }


    private boolean isCorrectAnswer(
            String studentAnswer,
            Question question
    ) {

        if (studentAnswer == null || studentAnswer.isBlank()) {
            return false;
        }

        String answer =
                studentAnswer
                        .trim()
                        .toLowerCase();

        if (question.getExpectedAnswer() != null &&
                answer.equals(
                        question.getExpectedAnswer()
                                .trim()
                                .toLowerCase()
                )) {

            return true;
        }

        return question.getAlternativeAnswers()
                .stream()
                .map(a -> a.trim().toLowerCase())
                .anyMatch(answer::equals);

    }

    @Override
    public ApiResponse<EmailResponse> submitPassage(
            String attemptId,
            SubmitPassageRequest request) {

        AssessmentAttempt attempt =
                studentSecurityService.getStudentAttempt(
                        attemptId
                );

        if (attempt.getStatus() != AttemptStatus.STARTED) {
            throw new RuntimeException("Assessment already submitted");
        }

        Assessment assessment =
                assessmentRepository
                        .findById(
                                attempt.getAssessmentId()
                        )
                        .orElseThrow(() ->
                                new AssessmentNotFoundException(
                                        "Assessment not found"
                                )
                        );

        List<StudentAnswer> answers =

                request.getAnswers()

                        .stream()

                        .map(answer ->

                                StudentAnswer.builder()

                                        .attemptId(
                                                attemptId
                                        )

                                        .questionId(
                                                answer.getQuestionId()
                                        )

                                        .answer(
                                                answer.getAnswer()
                                        )

                                        .obtainedMarks(
                                                0
                                        )

                                        .evaluationStatus(
                                                EvaluationStatus.PENDING
                                        )

                                        .feedback(
                                                List.of()
                                        )

                                        .build()

                        )

                        .toList();

        studentAnswerRepository.saveAll(
                answers
        );

        List<QuestionResponse> emails =

                questionRepository

                        .findByAssessmentIdAndTypeOrderByQuestionOrder(

                                attempt.getAssessmentId(),

                                QuestionType.EMAIL

                        )

                        .stream()

                        .map(
                                QuestionMapper::toResponse
                        )

                        .toList();

        EmailResponse response =

                EmailResponse.builder()

                        .writingTime(
                                assessment.getEmailWritingTime()
                        )

                        .emailQuestions(
                                emails
                        )

                        .build();

        return ApiResponse.<EmailResponse>builder()

                .success(true)

                .message("Passages submitted")

                .data(response)

                .build();

    }

    @Override
    public ApiResponse<AssessmentSubmittedResponse> submitEmail(
            String attemptId,
            SubmitEmailRequest request) {

        AssessmentAttempt attempt =
                studentSecurityService.getStudentAttempt(
                        attemptId
                );

        if (attempt.getStatus() != AttemptStatus.STARTED) {
            throw new RuntimeException("Assessment already submitted");
        }

        Assessment assessment =
                assessmentRepository.findById(attempt.getAssessmentId())
                        .orElseThrow(() ->
                                new AssessmentNotFoundException("Assessment not found"));



        List<StudentAnswer> answers =

                request.getAnswers()

                        .stream()

                        .map(answer ->

                                StudentAnswer.builder()

                                        .attemptId(
                                                attemptId
                                        )

                                        .questionId(
                                                answer.getQuestionId()
                                        )

                                        .answer(
                                                answer.getAnswer()
                                        )

                                        .obtainedMarks(
                                                0
                                        )

                                        .evaluationStatus(
                                                EvaluationStatus.PENDING
                                        )

                                        .feedback(
                                                List.of()
                                        )

                                        .build()

                        )

                        .toList();

        studentAnswerRepository.saveAll(
                answers
        );

        attempt.setStatus(AttemptStatus.SUBMITTED);
        attempt.setSubmittedAt(LocalDateTime.now());
        attempt.setEndReason(AttemptEndReason.SUBMITTED);

        assessmentAttemptRepository.save(attempt);
        evaluationService.evaluateAttemptAsync(attemptId);

        AssessmentSubmittedResponse response =
                AssessmentSubmittedResponse.builder()
                        .attemptId(attempt.getId())
                        .message("Assessment submitted successfully. Evaluation in progress.")
                        .build();

        return ApiResponse.<AssessmentSubmittedResponse>builder()
                .success(true)
                .message("Assessment submitted successfully")
                .data(response)
                .build();
    }

    @Override
    public ApiResponse<Void> terminateAssessment(
            String attemptId,
            TerminateAssessmentRequest request) {

        AssessmentAttempt attempt =
                studentSecurityService.getStudentAttempt(attemptId);

        if (attempt.getStatus() != AttemptStatus.STARTED) {
            throw new RuntimeException("Assessment already finished");
        }

        attempt.setStatus(AttemptStatus.TERMINATED);

        attempt.setEndReason(request.getReason());

        attempt.setSubmittedAt(LocalDateTime.now());

        assessmentAttemptRepository.save(attempt);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Assessment terminated")
                .data(null)
                .build();
    }


}