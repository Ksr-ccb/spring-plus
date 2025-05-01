package org.example.expert.domain.todo.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class TodoSearchRepositoryImpl implements TodoSearchRepository {

    private final EntityManager entityManager;

    @Override
    public Page<Todo> searchTodos(String weather, LocalDate startDate, LocalDate endDate, Pageable pageable) {
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
        jpql.append("ORDER BY t.modifiedAt DESC");

        TypedQuery<Todo> query = entityManager.createQuery(jpql.toString(), Todo.class);
        params.forEach(query::setParameter);

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<Todo> result = query.getResultList();

        return new PageImpl<>(result, pageable, result.size());
    }
}
