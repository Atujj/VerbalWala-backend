package com.verbalwala.backend.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AssessmentStudentResponse {

    private String attemptId;

    private String studentName;

    private Integer score;

    private Integer totalMarks;

}