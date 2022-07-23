package com.todoay.api.domain.auth.exception;

import com.todoay.api.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {


    EMAIL_DUPLICATE(HttpStatus.BAD_REQUEST, "이미 사용 중인 이메일입니다.");


    private final HttpStatus httpStatus;
    private final String detailMessage;


}
