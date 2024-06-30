package com.superkele.demo.paramhandler;

import com.superkele.translation.core.exception.ParamHandlerException;
import com.superkele.translation.core.mapping.ParamHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IntToStringParamHandler implements ParamHandler<Integer, String> {

    @Override
    public String wrapper(Integer param, Class<Integer> sourceClazz, Class<String> targetClazz, Class[] types) throws ParamHandlerException {
        return String.valueOf(param);
    }

    @Override
    public String wrapperBatch(List<Integer> params, Class<Integer> sourceClazz, Class<String> targetClazz, Class[] types) throws ParamHandlerException {
        return "";
    }
}
