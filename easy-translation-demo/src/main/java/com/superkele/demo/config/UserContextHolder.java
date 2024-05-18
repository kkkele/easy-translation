package com.superkele.demo.config;

import com.superkele.translation.core.thread.ContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;



@Component
public class UserContextHolder implements ContextHolder {
    @Override
    public Object getContext() {
        return RequestContextHolder.getRequestAttributes();

    }

    @Override
    public void passContext(Object context) {
        RequestContextHolder.setRequestAttributes((RequestAttributes) context);
    }

    @Override
    public void clearContext() {
        RequestContextHolder.resetRequestAttributes();
    }
}
