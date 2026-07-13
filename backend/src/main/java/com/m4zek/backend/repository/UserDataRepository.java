package com.m4zek.backend.repository;

import com.m4zek.backend.model.UserData;

public interface UserDataRepository {
    UserData save(UserData userData);
}
