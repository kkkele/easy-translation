package com.superkele.demo.processor_contextholder;

import com.superkele.translation.annotation.Mapping;
import lombok.Data;

@Data
public class AsyncContextEntity {

    @Mapping(translator = "getToken", async = true)
    private String token;

    @Mapping(translator = "getStartTime", async = true)
    private Long startTime;


}
