package com.m4zek.backend.model.dto.admin;

import com.m4zek.backend.model.User;
import com.m4zek.backend.model.UserStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record AccountListItemResponse(
        Integer id,
        String avatarUrl,
        String name,
        String email,
        StatusExtends status,
        List<String> roles,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public record StatusExtends(
            UserStatus value,
            String extendedText
    ){
        public StatusExtends(User user) {
            this(user.getStatus(),
                    switch (user.getStatus()) {
                        case SUSPENDED -> {
                            DateTimeFormatter formatter =
                                    DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
                                    yield user.getSuspendedTo().format(formatter);
                        }

                        default -> null;
                    });
        }
    }
}
