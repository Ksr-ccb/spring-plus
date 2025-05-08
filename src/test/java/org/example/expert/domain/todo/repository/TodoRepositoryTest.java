package org.example.expert.domain.todo.repository;

import jakarta.persistence.EntityManager;
import org.example.expert.config.JwtUtil;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.testconfig.TestSecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@Import(TestSecurityConfig.class)
class TodoRepositoryTest {

    @Autowired
    EntityManager entityManager;

    @Autowired
    TodoRepository todoRepository;

    @MockBean
    JwtUtil jwtUtil;

    private Long todo1Id;

    @BeforeEach
    void setUp() {
        AuthUser authUser = new AuthUser(null, "email", "nick", UserRole.ROLE_USER);
        User user = User.fromAuthUser(authUser);
        entityManager.persist(user);
        entityManager.flush(); // ID 자동 생성

        Todo todo1 = new Todo("title1", "content1", "RAINY", user);
        entityManager.persist(todo1);

        Todo todo2 = new Todo("title2", "content2", "SUNNY", user);
        entityManager.persist(todo2);

        entityManager.flush();

        todo1Id = todo1.getId();
        entityManager.clear();
    }

    @Nested
    @DisplayName("GetTodosTests")
    class GetTodosTests {
        @Test
        @DisplayName(" searchTodos 성공 - 조건 다있음")
        void searchTodos_success_withAllard(){

            //given
            String weather = "RAINY";
            LocalDateTime startDate =  LocalDateTime.of(2025, 4, 19, 13, 30, 0);
            LocalDateTime endDate = LocalDateTime.of(2025, 5, 9, 13, 30, 0);
            Pageable pageable = PageRequest.of(0,10);

            //when
            Page<Todo> todos = todoRepository.getTodos(weather, startDate, endDate, pageable);


            //System.out.println("startDate = " + startDate);
            //System.out.println("endDate = " + endDate);

            //then
            assertThat(todos.getContent()).hasSize(1);
            assertThat(todos.getContent().get(0).getTitle()).isEqualTo("title1");
        }

        @Test
        @DisplayName(" searchTodos 성공 - 조건 다 없음")
        void searchTodos_success_NoArgument(){
            //given
            Pageable pageable = PageRequest.of(0,10);
            //when
            Page<Todo> todos = todoRepository.getTodos(null, null, null, pageable);

            //then
            assertThat(todos.getContent()).hasSize(2);
        }
    }

    @Nested
    @DisplayName("GetTodoTests")
    class GetTodoTests {

        @Test
        @DisplayName("getTodo 성공 - 존재하는 Todo ID")
        void getTodo_Success() {
            // given
            Long todoId = todo1Id;  // 첫 번째 todoId

            // when
            Todo todo = todoRepository.getTodo(todoId);

            // then
            assertThat(todo).isNotNull();
            assertThat(todo.getTitle()).isEqualTo("title1");
            assertThat(todo.getContents()).isEqualTo("content1");
        }

        @Test
        @DisplayName("getTodo 실패 - 존재하지 않는 Todo ID")
        void getTodo_Failure() {
            // given
            long todoId = 999L;  // 존재하지 않는 todoId

            // when & then
            InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
                todoRepository.getTodo(todoId);
            });

            assertThat(exception.getMessage()).isEqualTo("Todo not found");
        }
    }

    @Nested
    @DisplayName("GetTodosTests")
    class searchTodosTests{

        @Test
        @DisplayName("searchTodos - 제목에 'ti' 포함 조건")
        void searchTodos_titleCondition() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<TodoSearchResponse> result = todoRepository.searchTodos(pageable, null, "ti", null, null);

            assertThat(result).hasSize(2);
            assertThat(result.getContent().get(0).getTitle()).contains("ti");
        }

        @Test
        @DisplayName("searchTodos - 날짜 조건 적용")
        void searchTodos_dateRangeCondition() {
            Pageable pageable = PageRequest.of(0, 10);

            LocalDateTime start = LocalDateTime.of(2024, 5, 1, 0, 0);
            LocalDateTime end = LocalDateTime.of(2024, 5, 1, 23, 59);

            Page<TodoSearchResponse> result = todoRepository.searchTodos(pageable, null, null, start, end);

            assertThat(result).hasSize(0);
        }

        @Test
        @DisplayName("searchTodos - 페이징 확인")
        void searchTodos_pagingTest() {
            Pageable pageable = PageRequest.of(0, 1); // 한 페이지당 1개

            Page<TodoSearchResponse> result = todoRepository.searchTodos(pageable, null, null, null, null);

            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalPages()).isEqualTo(2);
        }
    }

}