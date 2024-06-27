package com.secondwind.prototype.api.controller;

import com.secondwind.prototype.api.domain.request.SignUpRequest;
import com.secondwind.prototype.api.domain.request.TokenRequestDTO;
import com.secondwind.prototype.api.domain.response.MemberResponse;
import com.secondwind.prototype.api.service.AuthService;
import com.secondwind.prototype.common.dto.TokenDTO;
import com.secondwind.prototype.common.exception.code.AuthErrorCode;
import com.secondwind.prototype.common.exception.code.MemberErrorCode;
import com.secondwind.prototype.common.result.ResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "auth", description = "Auth API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

  private final String BASIC_PREFIX = "Basic ";
  private final AuthService authService;

  @Operation(summary = "회원가입 API")
  @PostMapping("/signup")
  public ResponseEntity<?> signup(
      @RequestHeader(value = HttpHeaders.AUTHORIZATION) String authorization,
      @RequestBody SignUpRequest signUpRequest) {

    String authBasic = authorization.substring(BASIC_PREFIX.length());
    String decodedAuthBasic = new String(Base64.getDecoder().decode(authBasic),
        StandardCharsets.UTF_8);
    String[] authUserInfo = decodedAuthBasic.split(":");

    String loginId = authUserInfo[0];
    String password = authUserInfo[1];

    signUpRequest.setLoginId(loginId);
    signUpRequest.setPassword(password);

    Long id = authService.signup(signUpRequest);
    return ResponseHandler.generate()
        .data(id)
        .status(HttpStatus.CREATED)
        .build();
  }

  @Operation(summary = "로그인 API")
  @PostMapping("/login")
  @ApiResponse(
      responseCode = "200",
      description = "성공",
      content = @Content(
          schema = @Schema(
              implementation = TokenDTO.class)
      )
  )
  public ResponseEntity<?> login(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
    String authBasic = authorization.substring(BASIC_PREFIX.length());

    String decodedAuthBasic = new String(Base64.getDecoder().decode(authBasic),
        StandardCharsets.UTF_8);
    String[] authUserInfo = decodedAuthBasic.split(":");

    String loginId = authUserInfo[0];
    String password = authUserInfo[1];

    TokenDTO tokenDTO = authService.login(loginId, password);
    return ResponseHandler.generate()
        .data(tokenDTO)
        .status(HttpStatus.OK)
        .build();
  }

  @Operation(summary = "토큰 재발급 API")
  @PostMapping("/reissue")
  @ApiResponse(
      responseCode = "200",
      description = "성공",
      content = @Content(
          schema = @Schema(
              implementation = TokenDTO.class)
      )
  )
  public ResponseEntity<?> reissue(@Valid @RequestBody TokenRequestDTO tokenRequestDTO) {
    TokenDTO tokenDTO = authService.reissue(tokenRequestDTO);

    return ResponseHandler.generate()
        .data(tokenDTO)
        .status(HttpStatus.OK)
        .build();
  }

  @Operation(summary = "로그아웃 API")
  @PostMapping("/logout")
  @ApiResponse(
      responseCode = "200",
      description = "성공",
      content = @Content(
          schema = @Schema(
              implementation = MemberResponse.class)
      )
  )
  public ResponseEntity<?> logout(@Valid @RequestBody TokenRequestDTO tokenRequestDTO) {
    authService.logout(tokenRequestDTO);

    return ResponseHandler.generate()
        .data(null)
        .status(HttpStatus.OK)
        .build();
  }

  @Operation(summary = "비밀번호 변경 API")
  @PatchMapping("/password")
  @ApiResponse(
      responseCode = "200",
      description = "성공"
  )
  public ResponseEntity<?> updatePassword(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
    String authBasic = authorization.substring(BASIC_PREFIX.length());
    String decodedAuthBasic = new String(Base64.getDecoder().decode(authBasic),
        StandardCharsets.UTF_8);
    String[] authUserInfo = decodedAuthBasic.split(":");

    String loginId = authUserInfo[0];
    String password = authUserInfo[1];

    authService.updatePassword(loginId, password);

    return ResponseHandler.generate()
        .data(null)
        .status(HttpStatus.OK)
        .build();
  }
}
