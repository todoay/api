package com.todoay.api.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@Builder
public class AuthSendEmailRequestDto {
    @Schema(required = true)
    @NotBlank
    @Email
    private String email;
}
