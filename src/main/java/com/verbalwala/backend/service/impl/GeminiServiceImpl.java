package com.verbalwala.backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.verbalwala.backend.dto.ai.AiEvaluationResponse;
import com.verbalwala.backend.dto.gemini.Content;
import com.verbalwala.backend.dto.gemini.GeminiRequest;
import com.verbalwala.backend.dto.gemini.GeminiResponse;
import com.verbalwala.backend.dto.gemini.Part;
import com.verbalwala.backend.exception.GeminiException;
import com.verbalwala.backend.service.GeminiService;
import com.verbalwala.backend.service.PromptBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;


@Service
@RequiredArgsConstructor
public class GeminiServiceImpl implements GeminiService {

    private final RestClient geminiRestClient;

    private final ObjectMapper objectMapper;

    private final PromptBuilder promptBuilder;

    @Value("${gemini.model}")
    private String model;


    @Override
    public String askGemini(String prompt) {

        GeminiRequest request =
                new GeminiRequest(
                        List.of(
                                new Content(
                                        List.of(
                                                new Part(prompt)
                                        )
                                )
                        )
                );

        GeminiResponse response =
                geminiRestClient.post()
                        .uri("/{model}:generateContent",
                                model)
                        .body(request)
                        .retrieve()
                        .body(GeminiResponse.class);

        if (response == null
                || response.getCandidates() == null
                || response.getCandidates().isEmpty()) {

            throw new RuntimeException("No response from Gemini");
        }

        return response.getCandidates()
                .get(0)
                .getContent()
                .getParts()
                .get(0)
                .getText();
    }

    @Override
    public AiEvaluationResponse evaluatePassage(
            String passage,
            String studentAnswer) {

        try {

            String response = askGemini(
                    promptBuilder.buildPassagePrompt(
                            passage,
                            studentAnswer
                    )
            );

            response = cleanJson(response);


            return objectMapper.readValue(
                    response,
                    AiEvaluationResponse.class
            );

        } catch (Exception ex) {

            throw new GeminiException(
                    "Failed to evaluate passage",
                    ex
            );

        }

    }

    private String cleanJson(String response) {
        return response
                .replace("```json", "")
                .replace("```", "")
                .trim();
    }

    @Override
    public AiEvaluationResponse evaluateEmail(
            String emailPrompt,
            String studentAnswer) {

        try {

            String response = askGemini(
                    promptBuilder.buildEmailPrompt(
                            emailPrompt,
                            studentAnswer
                    )
            );

            response = cleanJson(response);


            return objectMapper.readValue(
                    response,
                    AiEvaluationResponse.class
            );

        } catch (Exception ex) {

            ex.printStackTrace();

            throw new GeminiException(
                    "Failed to evaluate email",
                    ex
            );

        }

    }
}