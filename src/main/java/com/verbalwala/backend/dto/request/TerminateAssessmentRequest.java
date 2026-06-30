package com.verbalwala.backend.dto.request;

import com.verbalwala.backend.enums.AttemptEndReason;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TerminateAssessmentRequest {

    @NotNull
    private AttemptEndReason reason;

}