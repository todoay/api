package com.todoay.api.domain.todo.controller;

import com.todoay.api.domain.todo.dto.daily.*;
import com.todoay.api.domain.todo.service.DailyTodoCRUDService;
import com.todoay.api.global.exception.ErrorResponse;
import com.todoay.api.global.exception.ValidErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequestMapping("/todo/daily")
@RequiredArgsConstructor
@RestController
public class DailyTodoController {

    private final DailyTodoCRUDService dailyTodoCRUDService;

    @PostMapping
    @Operation(
            summary = "로그인 유저의 DailyTodo를 추가한다.",
            responses = {
                    @ApiResponse(responseCode = "201"),  // 요청이 수용되어 리소스가 만들어졌을 때
                    @ApiResponse(responseCode = "400", description = "올바른 양식을 입력하지 않음.", content = @Content(schema = @Schema(implementation = ValidErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Access Token 만료", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "403", description = "카테고리가 로그인 유저의 것이 아님", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<DailyTodoSaveResponseDto> dailyTodoSave(@RequestBody @Validated DailyTodoSaveRequestDto dailyTodoSaveRequestDto) {
        log.info("hashtags = {}",dailyTodoSaveRequestDto.getHashtagNames());
        DailyTodoSaveResponseDto dailyTodoSaveResponseDto = dailyTodoCRUDService.addTodo(dailyTodoSaveRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(dailyTodoSaveResponseDto);
    }


    @GetMapping("/my/{id}")
    @Operation(
            summary = "DailyTodo 단건 조회",
            description = "ID로 DailyTodo를 조회한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = DailyTodoReadDetailResponseDto.class))),
                    @ApiResponse(responseCode = "401", description = "Access Token 만료", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "403", description = "id에 해당하는 TODO가 본인 것이 아니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "id에 해당하는 TODO가 존재하지 않는다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<DailyTodoReadDetailResponseDto> readDailyTodoById(@PathVariable("id") long id) {
        DailyTodoReadDetailResponseDto dto = dailyTodoCRUDService.readDailyTodoDetailById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/my")
    @Operation(
            summary = "DailyTodo 복수 조회",
            description = "특정 날짜에 대한 DailyTodo들을 조회한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = DailyTodoReadResponseDto.class))),
                    @ApiResponse(responseCode = "401", description = "Access Token 만료", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<List<DailyTodoReadResponseDto>> readDailyTodosByDate(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate localDate) {
        List<DailyTodoReadResponseDto> dtos = dailyTodoCRUDService.readDailyTodosByDate(localDate);
        return ResponseEntity.ok(dtos);
    }


    @PostMapping("/{id}/repeat")
    @Operation(
            summary = "dailyTodo 반복 생성",
            description = "DailyTodo를 반복 타입, 기간을 설정하여 타겟 Todo의 날짜를 기준으로 반복해서 생성한다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "반복 생성 성공"),
                    @ApiResponse(responseCode = "400", description = "1. 반복 조건 오류 \t\n 2. 올바른 양식 입력 X", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Access Token 만료", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "403", description = "1. 내 TODO가 아닌 TODO에 접근 \t\n 2. ENUM 변환 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "해당 ID에 리소스가 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ListenableFuture<ResponseEntity<Void>> repeatDailyTodoByCondition(@PathVariable("id") long id, @RequestBody @Validated DailyTodoRepeatRequestDto dto) {
        return dailyTodoCRUDService.repeatDailyTodo(id, dto);
    }
    @DeleteMapping("/{id}/repeat")
    @Operation(
            summary = "로그인 유저의 반복 그룹에 엮이 Daily Todo를 전부 삭제한다",
            responses = {
                    @ApiResponse(responseCode = "204"),
                    @ApiResponse(responseCode = "400", description = "올바른 양식을 입력하지 않음.", content = @Content(schema = @Schema(implementation = ValidErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Access Token 만료", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "403", description = "리소스가 로그인 유저의 것이 아님", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "해당 id의 리소스가 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<Void> deleteAllRepeatGroupById(@PathVariable("id") long id) {
        dailyTodoCRUDService.deleteAllRepeatedDailyTodo(id);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/{id}")
    @Operation(
            summary = "로그인 유저의 DailyTodo를 수정한다.",
            responses = {
                    @ApiResponse(responseCode = "204"),
                    @ApiResponse(responseCode = "400", description = "올바른 양식을 입력하지 않음.", content = @Content(schema = @Schema(implementation = ValidErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Access Token 만료", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "403", description = "카테고리가 로그인 유저의 것이 아님", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "해당 id의 리소스가 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<DailyTodoSaveResponseDto> dailyTodoModify(@RequestBody @Validated DailyTodoModifyRequestDto dailyTodoModifyRequestDto, @PathVariable Long id) {
        dailyTodoCRUDService.modifyDailyTodo(id, dailyTodoModifyRequestDto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/daily-date")
    @Operation(
            summary = "로그인 유저의 DailyTodo를 수정한다.",
            responses = {
                    @ApiResponse(responseCode = "204"),
                    @ApiResponse(responseCode = "400", description = "올바른 양식을 입력하지 않음.", content = @Content(schema = @Schema(implementation = ValidErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Access Token 만료", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "403", description = "리소스가 로그인 유저의 것이 아님", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "해당 id의 리소스가 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<Void> dailyTodoDailyDateModify(@PathVariable Long id, @RequestBody @Validated DailyTodoDailyDateModifyRequestDto dto) {
        dailyTodoCRUDService.modifyDailyDate(id, dto);
        return ResponseEntity.noContent().build();
    }


}
