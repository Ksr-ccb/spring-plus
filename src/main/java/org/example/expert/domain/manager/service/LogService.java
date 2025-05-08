package org.example.expert.domain.manager.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.manager.entity.Log;
import org.example.expert.domain.manager.enums.LogStatus;
import org.example.expert.domain.manager.repository.LogRepository;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;

    @Transactional(propagation = REQUIRES_NEW)
    public void saveLog(User user, Todo todo, LogStatus status){
        Log log = new Log(user, todo, status);
        logRepository.save(log);
    }
}
