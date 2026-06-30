package com.verbalwala.backend.controller;

import com.verbalwala.backend.dto.request.SubmitEmailRequest;
import com.verbalwala.backend.dto.request.SubmitFillBlankRequest;
import com.verbalwala.backend.dto.request.SubmitPassageRequest;
import com.verbalwala.backend.dto.request.TerminateAssessmentRequest;
import com.verbalwala.backend.dto.response.*;
import com.verbalwala.backend.service.StudentAssessmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student/assessments")
@RequiredArgsConstructor
public class StudentAssessmentController {

    private final StudentAssessmentService studentAssessmentService;

    @PostMapping("/{assessmentId}/start")
    public ResponseEntity<ApiResponse<StartAssessmentResponse>> startAssessment(
            @PathVariable String assessmentId) {

        return ResponseEntity.ok(
                studentAssessmentService.startAssessment(assessmentId)
        );
    }

    @PostMapping("/attempts/{attemptId}/submit-fill-blanks")
    public ResponseEntity<ApiResponse<PassageResponse>> submitFillBlanks(
            @PathVariable String attemptId,
            @Valid @RequestBody SubmitFillBlankRequest request) {

        return ResponseEntity.ok(
                studentAssessmentService.submitFillBlanks(
                        attemptId,
                        request
                )
        );
    }

    @PostMapping("/attempts/{attemptId}/submit-passage")
    public ResponseEntity<ApiResponse<EmailResponse>> submitPassage(
            @PathVariable String attemptId,
            @Valid @RequestBody SubmitPassageRequest request) {

        return ResponseEntity.ok(
                studentAssessmentService.submitPassage(
                        attemptId,
                        request
                )
        );
    }

    @PostMapping("/attempts/{attemptId}/submit-email")
    public ResponseEntity<ApiResponse<AssessmentSubmittedResponse>> submitEmail(
            @PathVariable String attemptId,
            @Valid @RequestBody SubmitEmailRequest request) {

        return ResponseEntity.ok(
                studentAssessmentService.submitEmail(
                        attemptId,
                        request
                )
        );
    }

    @PostMapping("/attempts/{attemptId}/terminate")
    public ResponseEntity<ApiResponse<Void>> terminateAssessment(
            @PathVariable String attemptId,
            @Valid @RequestBody TerminateAssessmentRequest request) {

        return ResponseEntity.ok(
                studentAssessmentService.terminateAssessment(
                        attemptId,
                        request
                )
        );

    }




}