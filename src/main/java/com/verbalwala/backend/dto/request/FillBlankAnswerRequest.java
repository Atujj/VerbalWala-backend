package com.verbalwala.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FillBlankAnswerRequest {

    @NotBlank(message = "Question ID is required")
    private String questionId;


    private String answer;

}