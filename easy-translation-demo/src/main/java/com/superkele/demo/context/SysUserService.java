package com.superkele.demo.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
public  class SysUserService implements UserService{

    @Override
    public User getUserById(Integer id) {
        log.info("调用了 sysUserService的getUserById方法");
        return new User()
                .setId(id)
                .setName(UUID.randomUUID().toString().substring(1, 6))
                .setAge(new Random().nextInt(100));
    }

    @Override
    public User getUserById2(Integer id) {
        log.info("调用了 sysUserService的getUserById2方法");
        return new User()
                .setId(id)
                .setName(UUID.randomUUID().toString().substring(1, 6))
                .setAge(new Random().nextInt(100));
    }
}
