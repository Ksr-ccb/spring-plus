package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import org.example.expert.domain.comment.entity.QComment;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.manager.entity.QManager;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class TodoSearchRepositoryImpl implements TodoSearchRepository {

    private final EntityManager entityManager;
    private final JPAQueryFactory jpaQueryFactory;

    public TodoSearchRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.jpaQueryFactory = new JPAQueryFactory(entityManager); // 생성자에서 초기화
    }

    @Override
    public Page<Todo> getTodos(String weather, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        StringBuilder jpql = new StringBuilder("SELECT t FROM Todo t LEFT JOIN FETCH t.user u ");
        Map<String, Object> params = new HashMap<>();
        List<String> whereList = new ArrayList<>();

        //날씨o
        if (weather != null && !weather.isBlank()) {
            whereList.add("t.weather = :weather");
            params.put("weather", weather);
        }
        // 시작날짜
        if (startDate != null) {
            whereList.add("t.modifiedAt >= :startDate");
            params.put("startDate", startDate);
        }
        //끝날짜
        if (endDate != null) {
            whereList.add("t.modifiedAt <= :endDate");
            params.put("endDate", endDate);
        }

        //조건 하나라도 있으면 적용해줘야함
        if (!whereList.isEmpty()) {
            jpql.append(" WHERE ").append(String.join(" AND ", whereList));
        }
        //정렬 조건 마지막에 넣어줌
        jpql.append(" ORDER BY t.modifiedAt DESC");

        TypedQuery<Todo> query = entityManager.createQuery(jpql.toString(), Todo.class);
        params.forEach(query::setParameter);

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<Todo> result = query.getResultList();

        return new PageImpl<>(result, pageable, result.size());
    }

    @Override
    public Todo getTodo(long todoId) {
        QTodo qTodo = QTodo.todo;

        Todo todo = jpaQueryFactory.selectFrom(qTodo)
                .leftJoin(qTodo.user).fetchJoin()
                .where(qTodo.id.eq(todoId))
                .fetchOne();

        if(todo == null){
            throw new InvalidRequestException("Todo not found");
        }

        return todo;
    }

    @Override
    public Page<TodoSearchResponse> searchTodos(Pageable pageable, String nickname, String title, LocalDateTime startDate, LocalDateTime endDate) {
        QTodo qTodo = QTodo.todo;
        QManager qManager = QManager.manager;
        QComment qComment = QComment.comment;

        List<TodoSearchResponse> result = jpaQueryFactory
                .select(Projections.fields(TodoSearchResponse.class,
                        qTodo.title.as("title"),
                        qManager.countDistinct().as("managerCount"),
                        qComment.countDistinct().as("commentCount")
                ))
                .from(qTodo)
                .leftJoin(qTodo.managers, qManager)
                .leftJoin(qTodo.comments, qComment)
                .groupBy(qTodo.id)
                .where(
                        StringUtils.hasText(title) ? qTodo.title.contains(title) : null,
                        StringUtils.hasText(nickname) ? qTodo.user.nickname.contains(nickname) : null,
                        dateBetween(startDate, endDate, qTodo)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalSize = jpaQueryFactory
                .select(qTodo.countDistinct())
                .from(qTodo)
                .leftJoin(qTodo.managers, qManager)
                .leftJoin(qTodo.comments, qComment)
                .where(
                        StringUtils.hasText(title) ? qTodo.title.contains(title) : null,
                        StringUtils.hasText(nickname) ? qTodo.user.nickname.contains(nickname) : null,
                        dateBetween(startDate, endDate, qTodo)
                )
                .fetchOne();

        return new PageImpl<>(result, pageable, totalSize);
    }

    private BooleanExpression dateBetween(LocalDateTime start, LocalDateTime end, QTodo qTodo) {
        if (start != null && end != null) {
            return qTodo.modifiedAt.between(start, end);
        } else if (start != null) {
            return qTodo.modifiedAt.goe(start);
        } else if (end != null) {
            return qTodo.modifiedAt.loe(end);
        } else {
            return null;
        }
    }


}
