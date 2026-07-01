package com.verbalwala.backend.dto.response;

import com.verbalwala.backend.enums.AssessmentStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AdminAssessmentDetailResponse {

    private String assessmentId;

    private String title;

    private String description;

    private AssessmentStatus status;

    private Integer maxAttempts;

    private Integer fillBlankTime;

    private Integer passageReadTime;

    private Integer passageWriteTime;

    private Integer emailWritingTime;

    private List<AdminQuestionResponse> questions;

}