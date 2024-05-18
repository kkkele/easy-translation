package com.superkele.demo.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.superkele.demo.domain.entity.SysUser;
import com.superkele.demo.service.UserService;
import com.superkele.translation.annotation.TransMapper;
import com.superkele.translation.annotation.Translation;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
public class UserServiceImpl implements UserService {


    @Translation(name = "getTime")
    public static Date getDate() {
        return DateUtil.date();
    }

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


    @Translation(name = "getStrV2")
    public String getByCondition(Integer id, String other) {
        return StrUtil.join(",",id,other);
    }

    @Translation(name = "getStrV3")
    public String getByCondition(Integer id, Integer other, String other2) {
        return StrUtil.join(",",id,other,other2);
    }

    @Translation(name = "getStrV4")
    public String getByCondition(String other, @TransMapper Integer id, Boolean other2) {
        return StrUtil.join(",",id,other,other2);
    }
}
