package com.verbalwala.backend.controller;

import com.verbalwala.backend.dto.response.ApiResponse;
import com.verbalwala.backend.dto.response.AssessmentCardResponse;
import com.verbalwala.backend.dto.response.StudentAssessmentCardResponse;
import com.verbalwala.backend.service.StudentAssessmentListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/assessments")
@RequiredArgsConstructor
public class StudentAssessmentListController {

    private final StudentAssessmentListService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<StudentAssessmentCardResponse>>> getAssessments(){

        return ResponseEntity.ok(
                service.getAssessments()
        );

    }

}