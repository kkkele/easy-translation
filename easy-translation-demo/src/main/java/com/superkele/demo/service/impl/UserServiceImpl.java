package com.superkele.demo.service.impl;

import cn.hutool.core.date.DateUtil;
import com.superkele.demo.domain.entity.SysUser;
import com.superkele.demo.service.UserService;
import com.superkele.translation.annotation.Translation;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {

    @Override
    @Translation(name = "getUser")
    @Cacheable(cacheNames = "user", key = "#id")
    public SysUser getById(Integer id) {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        SysUser sysUser = new SysUser();
        sysUser.setUserId(id);
        sysUser.setUsername("username" + DateUtil.date());
        sysUser.setNickName("nickName" + DateUtil.date());
        return sysUser;
    }
}
