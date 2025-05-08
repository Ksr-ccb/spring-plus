package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface TodoSearchRepository {

    Page<Todo> getTodos(String weather, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    Todo getTodo(long todoId);

    Page<TodoSearchResponse> searchTodos(Pageable pageable, String nickname, String title,LocalDateTime startDate, LocalDateTime endDate);
}
