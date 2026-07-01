package com.verbalwala.backend.controller;

import com.verbalwala.backend.dto.request.CreateQuestionRequest;
import com.verbalwala.backend.dto.response.ApiResponse;
import com.verbalwala.backend.service.AssessmentService;
import com.verbalwala.backend.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/assessments")
@RequiredArgsConstructor
public class AdminQuestionController {

    private final QuestionService questionService;

    private final AssessmentService assessmentService;

    @PostMapping("/{assessmentId}/questions")
    public ResponseEntity<ApiResponse<Void>> addQuestion(
            @PathVariable String assessmentId,
            @Valid @RequestBody CreateQuestionRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(questionService.addQuestion(assessmentId, request));
    }

    //update question
    @PutMapping("/questions/{questionId}")
    public ResponseEntity<ApiResponse<Void>> updateQuestion(
            @PathVariable String questionId,
            @Valid @RequestBody CreateQuestionRequest request) {


        return ResponseEntity.ok(
                questionService.updateQuestion(
                        questionId,
                        request
                )
        );
    }

    //Delete Question
    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(
            @PathVariable String questionId) {

        return ResponseEntity.ok(
                questionService.deleteQuestion(questionId)
        );
    }

    //Publish Assessment
    @PutMapping("/{assessmentId}/publish")
    public ResponseEntity<ApiResponse<Void>> publishAssessment(
            @PathVariable String assessmentId) {

        return ResponseEntity.ok(
                assessmentService.publishAssessment(assessmentId)
        );
    }

}