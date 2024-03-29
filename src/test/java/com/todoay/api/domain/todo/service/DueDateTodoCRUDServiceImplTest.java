package com.todoay.api.domain.todo.service;

import com.todoay.api.domain.auth.entity.Auth;
import com.todoay.api.domain.auth.repository.AuthRepository;
import com.todoay.api.domain.todo.dto.duedate.DueDateTodoReadDetailResponseDto;
import com.todoay.api.domain.todo.dto.duedate.DueDateTodoReadResponseDto;
import com.todoay.api.domain.todo.entity.DueDateTodo;
import com.todoay.api.domain.todo.entity.Importance;
import com.todoay.api.domain.todo.entity.Todo;
import com.todoay.api.domain.todo.exception.EnumMismatchException;
import com.todoay.api.domain.todo.exception.TodoNotFoundException;
import com.todoay.api.domain.todo.repository.DueDateTodoRepository;
import com.todoay.api.global.context.LoginAuthContext;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DueDateTodoCRUDServiceImplTest {

    @Mock
    DueDateTodoRepository dueDateTodoRepository;
    @Mock
    LoginAuthContext loginAuthContext;
    @Mock
    AuthRepository authRepository;
    @InjectMocks
    DueDateTodoCRUDServiceImpl service;

    @Test
    void readDueDateTodoByCondition_Order() throws Exception{
        //given
        setMockObjectForLoggedInAuth();

        //when
        String condition = "duedate";
        List<DueDateTodoReadResponseDto> dtos = service.readTodosOrderByCondition(condition);


        //then
        SoftAssertions.assertSoftly(sa -> {
            for (int i = 0; i < dtos.size()-1; i++) {
                sa.assertThat(compareDueDate(dtos.get(i),dtos.get(i+1))).isTrue();
            }
        });
    }

    @Test
    void readDueDateTodoByCondition_Importance() throws Exception{
        //given
        setMockObjectForLoggedInAuth();

        //when
        String condition = "importance";
        List<DueDateTodoReadResponseDto> dtos = service.readTodosOrderByCondition(condition);


        //then
        SoftAssertions.assertSoftly(softAssertions-> {
            softAssertions.assertThat(dtos.get(0).getImportance()).isEqualTo(Importance.HIGH);
            softAssertions.assertThat(dtos.get(1).getImportance()).isEqualTo(Importance.HIGH);
            softAssertions.assertThat(dtos.get(3).getImportance()).isEqualTo(Importance.MIDDLE);
            softAssertions.assertThat(dtos.get(6).getImportance()).isEqualTo(Importance.LOW);
        });
    }

    @Test
    void readDueDateTodoByCondition_Else() throws Exception{
        //given
        setMockObjectForLoggedInAuth();

        //when
        //then
        String condition = "difficult";
        assertThatThrownBy(() -> service.readTodosOrderByCondition(condition))
               .isInstanceOf(EnumMismatchException.class);
    }

    @Test
    void sortDueDateTodoOrderByDuedate() throws Exception {
        // given
        setMockObjectMethodForReadMultiObj();
        Auth loginAuth = loginAuthContext.getLoginAuth();
        List<DueDateTodo> todos = dueDateTodoRepository.findNotFinishedDueDateTodoByAuth(loginAuth);
        Method sortTodosOrderByDueDate = service.getClass().getDeclaredMethod("sortTodosOrderByDueDate",List.class);
        sortTodosOrderByDueDate.setAccessible(true);

        // when
        List<DueDateTodoReadResponseDto> dtos = (List<DueDateTodoReadResponseDto>) sortTodosOrderByDueDate.invoke(service, todos);

        // then
        SoftAssertions.assertSoftly(sa -> {
            for (int i = 0; i < dtos.size()-1; i++) {
                sa.assertThat(compareDueDate(dtos.get(i),dtos.get(i+1))).isTrue();
            }
        });
    }

    private boolean compareDueDate(DueDateTodoReadResponseDto prev, DueDateTodoReadResponseDto next) {
        boolean equalResult = prev.getDueDate().isEqual(next.getDueDate());
        boolean beforeResult = prev.getDueDate().isBefore(next.getDueDate());
        boolean result = equalResult || beforeResult;
        return result;
    }

    @Test
    void sortDueDateTodoOrderByImportance() throws Exception {
        //given
        setMockObjectMethodForReadMultiObj();
        Auth loginAuth = loginAuthContext.getLoginAuth();
        List<DueDateTodo> todos = dueDateTodoRepository.findNotFinishedDueDateTodoByAuth(loginAuth);
        Method sortTodosOrderByImportance = service.getClass().getDeclaredMethod("sortTodosOrderByImportance",List.class);
        sortTodosOrderByImportance.setAccessible(true);
        //when
        List<DueDateTodoReadResponseDto> dtos = (List<DueDateTodoReadResponseDto>) sortTodosOrderByImportance.invoke(service, todos);

        //then
        SoftAssertions.assertSoftly(softAssertions-> {
            softAssertions.assertThat(dtos.get(0).getImportance()).isEqualTo(Importance.HIGH);
            softAssertions.assertThat(dtos.get(1).getImportance()).isEqualTo(Importance.HIGH);
            softAssertions.assertThat(dtos.get(3).getImportance()).isEqualTo(Importance.MIDDLE);
            softAssertions.assertThat(dtos.get(6).getImportance()).isEqualTo(Importance.LOW);
        });
    }



    @Test
    void readDueDateDetail() throws Exception{
        // given
        setMockObjectMethodForDetailObj();
        Long id = 1L;

        // when
        DueDateTodoReadDetailResponseDto detail = service.readDueDateTodoDetail(id);

        // then
        assertThat(detail.getId()).isSameAs(1L);
    }

    @Test
    void readDueDateDetail_TodoNotFoundException () throws Exception{
        //given
        Long id = 1L;
        //when
        //then
        assertThatThrownBy(() -> service.readDueDateTodoDetail(id))
                .isInstanceOf(TodoNotFoundException.class);
    }
    
    @Test
    void readFinishedDueDate () throws Exception{
        //given
        setMockObjectMethodForReadFinishedObj();
        //when

        List<DueDateTodo> data = createData();
        List<DueDateTodoReadResponseDto> dtos = service.readFinishedTodos();
        //then

        SoftAssertions.assertSoftly(sa -> {
            for (int i = 0; i < dtos.size()-1; i++) {
                sa.assertThat(compareDueDate(dtos.get(i),dtos.get(i+1))).isTrue();
            }
            sa.assertThat(data.size()).isNotSameAs(dtos.size());
        });
        
    
    }

    private void setMockObjectMethodForDetailObj() throws Exception {
        given(dueDateTodoRepository.findById(any()))
                .willReturn(Optional.ofNullable(filterNotFinishedData().get(0)));
        given(loginAuthContext.getLoginAuth())
                .willReturn(createAuthData());
    }

    private void setMockObjectMethodForReadFinishedObj() throws Exception {
        given(authRepository.findByEmail(any()))
                .willReturn(Optional.of(createAuthData()));
        given(dueDateTodoRepository.findFinishedDueDateTodoByAuth(any()))
                .willReturn(filterFinishedData());
        given(loginAuthContext.getLoginAuth())
                .willReturn(createAuthData());
    }

    private void setMockObjectForLoggedInAuth() throws Exception{
        given(authRepository.findByEmail(any()))
                .willReturn(Optional.of(createAuthData()));
        given(dueDateTodoRepository.findNotFinishedDueDateTodoByAuth(any()))
                .willReturn(filterNotFinishedData());
        given(loginAuthContext.getLoginAuth())
                .willReturn(createAuthData());
    }


    private void setMockObjectMethodForReadMultiObj() throws Exception {
//        given()

        given(dueDateTodoRepository.findNotFinishedDueDateTodoByAuth(any()))
                .willReturn(filterNotFinishedData());
        given(loginAuthContext.getLoginAuth())
                .willReturn(createAuthData());
    }

    private Auth createAuthData() throws Exception {
        String password = new BCryptPasswordEncoder().encode("qwer1234!");
        Auth auth = Auth.builder()
                .email("test@naver.com")
                .password(password)
                .build();
        Field id = auth.getClass().getDeclaredField("id");
        id.setAccessible(true);
        id.set(auth,1L);
        return auth;
    }

    private List<DueDateTodo> filterNotFinishedData() throws Exception {
        List<DueDateTodo> data = createData();
        return data.stream().filter(todo -> !todo.isFinished())
                .collect(Collectors.toList());
    }

    private List<DueDateTodo> filterFinishedData() throws Exception {
        List<DueDateTodo> data = createData();
        return data.stream().filter(Todo::isFinished)
                .collect(Collectors.toList());
    }
    private List<DueDateTodo> createData() throws Exception{
        List<DueDateTodo> list = new ArrayList<>();
        Auth auth = createAuthData();
        for (int i = 1; i < 10; i++) {
            DueDateTodo.DueDateTodoBuilder builder = DueDateTodo.builder()
                    .auth(null)
                    .dueDate(LocalDate.of(2022, 8, i))
                    .title("테스트" + i)
                    .description("테스트입니당" + i)
                    .isPublic(true)
                    .auth(auth);


            if(i<4)
                builder.importance(Importance.LOW);
            else if(i<6)
                builder.importance(Importance.MIDDLE);
            else
                builder.importance(Importance.HIGH);

            if(i>8)
                builder.isFinished(true);
            else
                builder.isFinished(false);


            DueDateTodo todo = builder.build();


            Field id = todo.getClass().getSuperclass().getDeclaredField("id");
            id.setAccessible(true);
            id.set(todo,Integer.toUnsignedLong(i));
            list.add(todo);
        }
        return list;
    }
}