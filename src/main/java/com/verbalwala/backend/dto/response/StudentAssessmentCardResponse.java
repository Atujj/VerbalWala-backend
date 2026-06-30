package com.verbalwala.backend.dto.response;

import com.verbalwala.backend.enums.AttemptStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class StudentAssessmentCardResponse {

    private String assessmentId;

    private String title;

    private String description;

    private Integer duration;

    private Integer totalQuestions;

    private Integer maxAttempts;

    private Integer attemptsUsed;

    private AttemptStatus latestStatus;

    private List<AttemptSummaryResponse> attempts;



}