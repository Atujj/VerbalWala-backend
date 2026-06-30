package com.verbalwala.backend.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudentDashboardResponse {

    private Integer completedAssessments;

    private Integer pendingAssessments;

    private Integer averageScore;

    private Integer bestScore;

}