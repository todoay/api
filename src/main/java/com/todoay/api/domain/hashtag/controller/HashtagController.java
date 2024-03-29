package com.todoay.api.domain.hashtag.controller;

import com.todoay.api.domain.hashtag.dto.HashtagSearchResponseDto;
import com.todoay.api.domain.hashtag.service.HashtagService;
import com.todoay.api.global.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/hashtag")
public class HashtagController {

    private final HashtagService hashtagService;

    @Operation(
            summary = "해시태그 검색 기능",
            description = "유저가 해시태그를 검색하면 리스트로 보여준다. 다음 페이지가 존재하면 다음 페이지를 검색한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공",  content = @Content(schema = @Schema(implementation = HashtagSearchResponseDto.class))),
                    @ApiResponse(responseCode = "401", description = "Access Token 만료", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "403", description = "허용되지 않은 접근", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping
    public ResponseEntity<HashtagSearchResponseDto> searchHashtag(@RequestParam("name") @NotBlank String name
            , @RequestParam("pageNum") @Min(0) int pageNum, @RequestParam("quantity") @Min(1) int quantity) {
        HashtagSearchResponseDto dto = hashtagService.searchHashtag(name, pageNum, quantity);
        return ResponseEntity.ok(dto);
    }

}
