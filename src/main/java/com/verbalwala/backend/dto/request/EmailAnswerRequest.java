package com.verbalwala.backend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailAnswerRequest {

    private String questionId;

    private String answer;

}