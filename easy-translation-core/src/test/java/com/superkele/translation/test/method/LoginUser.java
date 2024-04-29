package com.superkele.translation.test.method;

import com.superkele.translation.annotation.Translation;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LoginUser {

    String name;

    @Translation(name = "getName")
    public String getName(){
        return name;
    }
}
