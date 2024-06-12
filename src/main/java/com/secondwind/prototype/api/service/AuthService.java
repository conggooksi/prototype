package com.secondwind.prototype.api.service;

import com.secondwind.prototype.api.domain.request.SignUpRequest;

public interface AuthService {
    Long signup(SignUpRequest signUpRequest);
}
