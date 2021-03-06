package com.todoay.api.domain.auth.exception;

import com.todoay.api.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {


    EMAIL_DUPLICATE(HttpStatus.BAD_REQUEST, "이미 사용 중인 이메일입니다."),
    LOGIN_FAILED(HttpStatus.NOT_FOUND, "로그인에 실패하였습니다."),

    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "전달하신 RefreshToken은 존재하지 않습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String detailMessage;


}
