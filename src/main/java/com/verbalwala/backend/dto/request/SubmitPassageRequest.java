package com.verbalwala.backend.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SubmitPassageRequest {

    private List<PassageAnswerRequest> answers;

}