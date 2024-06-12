package com.secondwind.prototype.api.controller;

import com.secondwind.prototype.api.domain.request.SearchMember;
import com.secondwind.prototype.api.domain.response.MemberResponse;
import com.secondwind.prototype.api.service.MemberService;
import com.secondwind.prototype.common.result.JsonResultData;
import com.secondwind.prototype.common.result.ResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
@Slf4j
public class MemberController {

  private final MemberService memberService;

  @Operation(summary = "유저 목록 조회 API")
  @GetMapping("")
  @ApiResponse(
      responseCode = "200",
      description = "성공",
      content = @Content(
          array = @ArraySchema(
              schema = @Schema(
                  implementation = MemberResponse.class)
          )
      )
  )
  public ResponseEntity<JsonResultData<?>> getMembers(
      SearchMember searchMember) {
    Page<MemberResponse> members = memberService.getMembers(searchMember);
    return ResponseHandler.generate()
        .data(members)
        .status(HttpStatus.OK)
        .build();
  }

  @Operation(summary = "유저 조회 API")
  @GetMapping("/{member_id}")
  @ApiResponse(
      responseCode = "200",
      description = "성공",
      content = @Content(
          schema = @Schema(implementation = MemberResponse.class)
      )
  )
  public ResponseEntity<?> getMember(@PathVariable(value = "member_id") Long memberId) {
    return ResponseHandler.generate()
        .data(memberService.getMember(memberId))
        .status(HttpStatus.OK)
        .build();
  }

}
