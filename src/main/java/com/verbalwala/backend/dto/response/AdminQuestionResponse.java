package com.verbalwala.backend.dto.response;

import com.verbalwala.backend.enums.QuestionType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AdminQuestionResponse {

    private String id;

    private QuestionType type;

    private String questionText;

    private String expectedAnswer;

    private List<String> alternativeAnswers;

    private Integer marks;

    private Integer questionOrder;

}