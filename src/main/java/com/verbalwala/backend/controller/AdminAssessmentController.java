package com.verbalwala.backend.controller;

import com.verbalwala.backend.dto.request.CreateAssessmentRequest;
import com.verbalwala.backend.dto.response.AdminAssessmentCardResponse;
import com.verbalwala.backend.dto.response.AdminAssessmentDetailResponse;
import com.verbalwala.backend.dto.response.ApiResponse;
import com.verbalwala.backend.dto.response.AssessmentStudentResponse;
import com.verbalwala.backend.service.AdminAssessmentDetailService;
import com.verbalwala.backend.service.AdminAssessmentListService;
import com.verbalwala.backend.service.AssessmentService;
import com.verbalwala.backend.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/assessments")
@RequiredArgsConstructor
public class AdminAssessmentController {

    private final AdminAssessmentDetailService
            adminAssessmentDetailService;

    private final AssessmentService assessmentService;
    private final QuestionService questionService;

    private final AdminAssessmentListService adminAssessmentListService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> createAssessment(
            @Valid @RequestBody CreateAssessmentRequest request) {


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(assessmentService.createAssessment(request));
    }

    @GetMapping
    public ApiResponse<List<AdminAssessmentCardResponse>> getAssessments() {

        return adminAssessmentListService.getAssessments();

    }

    @GetMapping("/{assessmentId}")
    public ApiResponse<AdminAssessmentDetailResponse>
    getAssessment(

            @PathVariable String assessmentId
    ) {

        return adminAssessmentDetailService
                .getAssessment(
                        assessmentId
                );

    }

    @GetMapping("/{assessmentId}/students")
    public ResponseEntity<ApiResponse<List<AssessmentStudentResponse>>> getStudents(
            @PathVariable String assessmentId) {

        return ResponseEntity.ok(

                ApiResponse.<List<AssessmentStudentResponse>>builder()

                        .success(true)

                        .message("Students fetched successfully")

                        .data(
                                assessmentService.getStudents(
                                        assessmentId
                                )
                        )

                        .build()

        );

    }
}