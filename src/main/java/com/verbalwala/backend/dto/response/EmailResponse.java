package com.verbalwala.backend.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class EmailResponse {

    private Integer writingTime;

    private List<QuestionResponse> emailQuestions;

}