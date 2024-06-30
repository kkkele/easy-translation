package com.superkele.demo.context;

import com.superkele.translation.annotation.Translation;

public interface UserService {

    @Translation(name = "getUserById")
    User getUserById(Integer id);

    @Translation(name = "getUserById2",invokeBeanName = "appUserService")
    User getUserById2(Integer id);
}
