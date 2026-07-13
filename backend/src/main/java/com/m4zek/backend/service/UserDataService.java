package com.m4zek.backend.service;

import com.m4zek.backend.model.UserData;
import com.m4zek.backend.repository.UserDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class UserDataService {

    private final static Logger logger = LoggerFactory.getLogger(UserDataService.class);

    private final UserDataRepository repository;

    public UserDataService(UserDataRepository repository) {
        this.repository = repository;
    }

    public UserData save(UserData userData){
        UserData savedUserData = this.repository.save(userData);
        logger.info("User details has been saved [{}]", savedUserData.getId());
        return savedUserData;
    }


}
