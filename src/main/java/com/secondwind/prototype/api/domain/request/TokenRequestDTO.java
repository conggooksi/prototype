package com.secondwind.prototype.api.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenRequestDTO {
  @NotBlank(message = "잘못된 요청입니다.")
  String accessToken;
  @NotBlank(message = "잘못된 요청입니다.")
  String refreshToken;
}
