package com.m4zek.backend.repository;

import com.m4zek.backend.model.LoginHistory;

public interface LoginHistoryRepository {

    LoginHistory save(LoginHistory loginHistory);

}
