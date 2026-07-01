package com.verbalwala.backend.dto.response;

import com.verbalwala.backend.enums.AssessmentStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminAssessmentCardResponse {

    private String assessmentId;

    private String title;

    private String description;

    private AssessmentStatus status;

    private Integer fillBlankCount;

    private Integer passageCount;

    private Integer emailCount;

    private Integer studentCount;

}