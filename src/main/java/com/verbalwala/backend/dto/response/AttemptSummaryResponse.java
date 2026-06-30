package com.verbalwala.backend.dto.response;

import com.verbalwala.backend.enums.AttemptStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AttemptSummaryResponse {

    private String attemptId;

    private Integer attemptNumber;

    private AttemptStatus status;

    private Integer overallScore;

    private Double percentage;

    private LocalDateTime submittedAt;

    private boolean canViewResult;

}
