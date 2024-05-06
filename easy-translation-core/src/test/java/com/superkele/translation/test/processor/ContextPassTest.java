package com.superkele.translation.test.processor;

import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.annotation.Translation;
import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.context.support.DefaultTransExecutorContext;
import com.superkele.translation.core.processor.support.DefaultTranslationProcessor;
import com.superkele.translation.core.thread.ContextHolder;
import com.superkele.translation.test.processor.entity.User;
import com.superkele.translation.test.processor.service.OperateService;
import com.superkele.translation.test.processor.service.ShopService;
import com.superkele.translation.test.processor.service.UserService;
import lombok.Data;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.Executors;

public class ContextPassTest {

    public static final ThreadLocal<User> LOGIN_USER = new ThreadLocal<>();
    static UserService userService = new UserService();
    static OperateService operateService = new OperateService();
    static ShopService shopService = new ShopService();
    static DefaultTransExecutorContext asyncContext = DefaultTransExecutorContext.builder()
            .invokeObjs(userService, shopService, operateService)
            .packages("com.superkele.translation.test.processor")
            .config(new Config().setThreadPoolExecutor(Executors.newCachedThreadPool()))
            .build();
    static DefaultTranslationProcessor asyncProcessor = new DefaultTranslationProcessor(asyncContext);

    static{
        ContextHolder contextHolder = new ContextHolder();
        contextHolder.setConsumer(obj -> LOGIN_USER.set((User) obj));
        contextHolder.setSupplier(ContextPassTest::getLoginUser);
        asyncProcessor.addContextHolders(contextHolder);
    }


    @Translation(name = "login_user")
    public static User getLoginUser() {
        return LOGIN_USER.get();
    }

    @Test
    public void test() {
        login();
        UploadVO uploadVO = new UploadVO();
        asyncProcessor.process(uploadVO);
        System.out.println(uploadVO);
        logout();
    }

    public void login() {
        int random = new Random().nextInt();
        LOGIN_USER.set(new User(random, "user" + random, random));
    }

    public void logout() {
        LOGIN_USER.remove();
    }

    @Data
    public static class UploadVO {

        private Integer productId;

        @Mapping(translator = "login_user", receive = "id",async = true)
        private Integer userId;


        @Mapping(translator = "login_user", receive = "username",async = true)
        private String userName;

    }
}
