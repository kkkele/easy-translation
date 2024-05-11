package com.superkele.demo.service.impl;

import com.superkele.demo.domain.entity.SysUser;
import com.superkele.demo.service.UserService;
import com.superkele.translation.boot.annotation.Translator;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {

    @Override
    @Translator("getUser")
    public SysUser getById(Integer id) {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        SysUser sysUser = new SysUser();
        sysUser.setUserId(id);
        sysUser.setUsername("username" + id);
        sysUser.setNickName("nickName" + id);
        return sysUser;
    }
}
