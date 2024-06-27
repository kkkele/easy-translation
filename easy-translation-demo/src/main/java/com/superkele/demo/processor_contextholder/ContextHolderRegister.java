package com.superkele.demo.processor_contextholder;

import com.superkele.translation.core.thread.ContextHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ContextHolderRegister {

    @Bean
    public ContextHolder<String> securityContextHolder() {
        return new ContextHolder<String>() {
            @Override
            public String getContext() {
                return SecurityContext.getToken();
            }

            @Override
            public void passContext(String context) {
                SecurityContext.setToken(context);
            }

            @Override
            public void clearContext() {
                SecurityContext.clear();
            }
        };
    }

    @Bean
    public ContextHolder<Long> timeRecorderHolder() {
        return new ContextHolder<Long>() {
            @Override
            public Long getContext() {
                return TimeRecorder.get();
            }

            @Override
            public void passContext(Long context) {
                TimeRecorder.set(context);
            }

            @Override
            public void clearContext() {
                TimeRecorder.clear();
            }
        };
    }
}
