package com.todoay.api.global.exception;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
public class ErrorResponse {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int status;
    private final String error;
    private final String code;
    private final String message;
    private final String path;

    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode, String path) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponse.builder()
                        .status(errorCode.getHttpStatus().value())
                        .error(errorCode.getHttpStatus().name())
                        .code(errorCode.name())
                        .message(errorCode.getDetailMessage())
                        .path(path)
                        .build()
                );
    }
}