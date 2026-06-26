package com.verbalwala.backend.service;

import com.verbalwala.backend.dto.request.SignupRequest;

public interface UserService {

    void signup(SignupRequest request);

}