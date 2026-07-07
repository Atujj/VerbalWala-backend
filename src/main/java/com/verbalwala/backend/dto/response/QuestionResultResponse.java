package com.verbalwala.backend.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class QuestionResultResponse {

    private String questionId;

    private String questionType;

    private String questionText;

    private Integer score;

    private List<String> feedback;

    private String studentAnswer;

    private String expectedAnswer;

    private List<String> alternativeAnswers;

}