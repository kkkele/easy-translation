package com.superkele.translation.test.processor.service;

import com.superkele.translation.annotation.Translation;
import com.superkele.translation.test.processor.entity.User;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class UserService {

    @Translation(name = "user_id_to_user")
    public User getUser(Integer id) {
        try {
            TimeUnit.MILLISECONDS.sleep(5);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new User(id, "user_" + id, new Random().nextInt(20));
    }
}
