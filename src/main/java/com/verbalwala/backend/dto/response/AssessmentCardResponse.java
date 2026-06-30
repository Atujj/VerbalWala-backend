package com.verbalwala.backend.dto.response;

import com.verbalwala.backend.enums.AttemptStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AssessmentCardResponse {

    private String id;

    private String title;

    private String description;

    private Integer duration;

    private Integer totalQuestions;

    private Integer maxAttempts;

    // New fields
    private Integer attemptsUsed;

    private AttemptStatus status;

    private Integer overallScore;

    private LocalDateTime submittedAt;

    private String latestAttemptId;

    private boolean canStartNewAttempt;

}