package com.secondwind.prototype.api.service;

import com.secondwind.prototype.api.domain.request.SignUpRequest;
import com.secondwind.prototype.api.domain.request.TokenRequestDTO;
import com.secondwind.prototype.common.dto.TokenDTO;

public interface AuthService {
    Long signup(SignUpRequest signUpRequest);

  TokenDTO login(String loginId, String password);

  TokenDTO reissue(TokenRequestDTO tokenRequestDTO);

  void logout(TokenRequestDTO tokenRequestDTO);

  void updatePassword(String loginId, String password);
}
