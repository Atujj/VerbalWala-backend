package com.verbalwala.backend.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminDashboardResponse {

    private Integer totalAssessments;

    private Integer publishedAssessments;

    private Integer totalStudents;

}