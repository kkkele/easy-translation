package com.superkele.demo.context;

import com.superkele.translation.annotation.Translation;
import com.superkele.translation.annotation.constant.InvokeBeanScope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
public class AppUserService implements UserService {

    @Translation(name = "produceRandomUser")
    public static User produceRandomUser(Integer id) {
        return new User()
                .setId(id)
                .setName(UUID.randomUUID().toString().substring(1, 6))
                .setAge(new Random().nextInt(100));
    }

    @Translation(name = "getUserByIdSingleton")
    public User getUserByIdSingleton(Integer id) {
        return new User()
                .setId(id)
                .setName(UUID.randomUUID().toString().substring(1, 6))
                .setAge(new Random().nextInt(100));
    }

    @Translation(name = "getUserByIdPrototype", scope = InvokeBeanScope.PROTOTYPE)
    public User getUserByIdPrototype(Integer id) {
        return new User()
                .setId(id)
                .setName(UUID.randomUUID().toString().substring(1, 6))
                .setAge(new Random().nextInt(100));
    }

    @Override
    public User getUserById(Integer id) {
        log.info("调用了 appUserService的getUserById方法");
        return new User()
                .setId(id)
                .setName(UUID.randomUUID().toString().substring(1, 6))
                .setAge(new Random().nextInt(100));
    }

    @Override
    public User getUserById2(Integer id) {
        log.info("调用了 appUserService的getUserById2方法");
        return new User()
                .setId(id)
                .setName(UUID.randomUUID().toString().substring(1, 6))
                .setAge(new Random().nextInt(100));
    }
}