package com.superkele.demo.test._04processor_contextholder;


import com.superkele.demo.EasyTranslationDemoApplication;
import com.superkele.demo.processor_contextholder.AsyncContextEntity;
import com.superkele.demo.processor_contextholder.SecurityContext;
import com.superkele.demo.processor_contextholder.TimeRecorder;
import com.superkele.translation.core.processor.support.DefaultTranslationProcessor;
import com.superkele.translation.core.thread.ContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@SpringBootTest(classes = {EasyTranslationDemoApplication.class, ContextPassProcessTest.ContextHolderRegister.class})
@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
public class ContextPassProcessTest {

    @Autowired
    private DefaultTranslationProcessor processor;

    /**
     * 测试异步上下文传递
     *
     * @see com.superkele.demo.processor_contextholder.AsyncContextEntity
     */
    @Test
    public void test_async_context_pass() {
        String token = "123456";
        SecurityContext.setToken(token);
        long startTime = System.currentTimeMillis();
        TimeRecorder.set(startTime);
        AsyncContextEntity asyncContextEntity = new AsyncContextEntity();
        processor.process(asyncContextEntity);
        Assert.assertEquals(token, asyncContextEntity.getToken());
        Assert.assertEquals(startTime, asyncContextEntity.getStartTime().longValue());
    }

    @Configuration
    public static class ContextHolderRegister {

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
}
