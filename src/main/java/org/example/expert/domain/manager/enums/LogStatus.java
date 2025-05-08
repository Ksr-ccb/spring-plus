package org.example.expert.domain.manager.enums;

import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.enums.UserRole;

import java.util.Arrays;

public enum LogStatus {
    SUCCESS, FAIL;

    public static LogStatus of(String status) {
        return Arrays.stream(LogStatus.values())
                .filter(r -> r.name().equalsIgnoreCase(status))
                .findFirst()
                .orElseThrow(() -> new InvalidRequestException("유효하지 않은 LogStatus"));
    }
}
