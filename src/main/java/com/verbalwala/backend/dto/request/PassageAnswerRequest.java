package com.verbalwala.backend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PassageAnswerRequest {

    private String questionId;

    private String answer;

}