package org.example.expert.domain.todo.repository;

import jakarta.persistence.EntityManager;
import org.example.expert.config.JwtUtil;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(addFilters = false)
class TodoRepositoryTest {

    @Autowired
    EntityManager entityManager;

    @Autowired
    TodoRepository todoRepository;

    @MockBean
    JwtUtil jwtUtil;


    @BeforeEach
    void setUp() {
        AuthUser authUser = new AuthUser(null, "email", "nick", UserRole.USER); // <- id = null
        User user = User.fromAuthUser(authUser);
        entityManager.persist(user);
        entityManager.flush(); // ID 자동 생성

        Todo todo1 = new Todo("title1", "content1", "RAINY", user);
        ReflectionTestUtils.setField(todo1, "modifiedAt", LocalDateTime.of(2023, 5, 1, 13, 30));
        entityManager.persist(todo1);

        Todo todo2 = new Todo("title2", "content2", "SUNNY", user);
        ReflectionTestUtils.setField(todo2, "modifiedAt", LocalDateTime.of(2025, 5, 1, 13, 30));
        entityManager.persist(todo2);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName(" searchTodos 성공 - 조건 다있음")
    void searchTodos_success_withAllard(){

        //given
        String weather = "RAINY";
        LocalDateTime startDate =  LocalDateTime.of(2025, 4, 1, 13, 30, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 5, 2, 13, 30, 0);
        Pageable pageable = PageRequest.of(0,10);

        //when
        Page<Todo> todos = todoRepository.searchTodos(weather, startDate, endDate, pageable);

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
        Page<Todo> todos = todoRepository.searchTodos(null, null, null, pageable);

        //then
        assertThat(todos.getContent()).hasSize(2);
    }
}