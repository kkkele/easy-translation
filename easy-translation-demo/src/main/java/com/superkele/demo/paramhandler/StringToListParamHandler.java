package com.superkele.demo.paramhandler;

import cn.hutool.core.collection.ListUtil;
import com.superkele.translation.core.exception.ParamHandlerException;
import com.superkele.translation.core.mapping.ParamHandler;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class StringToListParamHandler implements ParamHandler<String, List<String>> {
    @Override
    public List<String> wrapper(String param, Class<String> sourceClazz, Class<List<String>> targetClazz, Class[] types) throws ParamHandlerException {
        return ListUtil.of(param);
    }

    @Override
    public List<String> wrapperBatch(List<String> params, Class<String> sourceClazz, Class<List<String>> targetClazz, Class[] types) throws ParamHandlerException {
        return Collections.emptyList();
    }
}
