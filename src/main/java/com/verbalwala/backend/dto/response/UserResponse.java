package com.verbalwala.backend.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserResponse {

    private String id;

    private String fullName;

    private String email;

    private String role;

}