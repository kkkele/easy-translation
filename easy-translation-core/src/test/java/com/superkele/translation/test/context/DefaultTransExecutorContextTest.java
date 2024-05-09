package com.superkele.translation.test.context;

import com.superkele.translation.annotation.TransMapper;
import com.superkele.translation.annotation.TransValue;
import com.superkele.translation.annotation.Translation;
import com.superkele.translation.core.context.support.DefaultTransExecutorContext;
import com.superkele.translation.core.translator.handle.TranslateExecutor;
import com.superkele.translation.test.util.TimeRecorder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultTransExecutorContextTest {

    @Test
    public void test() {
        UserService userService = new UserService();
        DefaultTransExecutorContext context = DefaultTransExecutorContext.builder()
                .invokeObjs(userService)
                .packages("com.superkele.translation.test.context")
                .build();
        TranslateExecutor resCodeToMsg = context.findExecutor("res_code_to_msg");
        TranslateExecutor currentTime = context.findExecutor("current_time");
        TranslateExecutor userIdToUser = context.findExecutor("user_id_to_user");
        System.out.println(resCodeToMsg.execute("200"));
        System.out.println(currentTime.execute());
        System.out.println(userIdToUser.execute(1));
    }

    @Test
    public void performanceTest(){
        UserService userService = new UserService();
        DefaultTransExecutorContext context = DefaultTransExecutorContext.builder()
                .invokeObjs(userService)
                .build();
        TranslateExecutor userIdToUser = context.findExecutor("user_id_to_user");
        //让jvm充分预热
        for (int i = 0; i< 1000;i ++){
            userIdToUser.execute(1);
            userIdToUser.execute(1);
        }
        CompletableFuture[] completableFutures = new CompletableFuture[2];
        completableFutures[0] = CompletableFuture.runAsync(() -> {
            long record = TimeRecorder.record(() -> {
                userService.getUserById(1);
            }, 1000);
            System.out.println("originMethodCost" + record +"ms");
        });
        completableFutures[1] = CompletableFuture.runAsync(() -> {
            long record = TimeRecorder.record(() -> {
                userIdToUser.execute(1);
            }, 1000);
            System.out.println("translateMethodCost" + record +"ms");
        });
        CompletableFuture.allOf(completableFutures).join();
        /**
         * 结果1:
         * originMethodCost6502ms
         * translateMethodCost6540ms
         * 结果2:
         * originMethodCost6482ms
         * translateMethodCost6512ms
         * 结果3:
         * originMethodCost6510ms
         * translateMethodCost6515ms
         * 可以看到，方法转成翻译器后会有一点的性能下降，当这些性能差距放大1000倍后，也只是ms级别的差别
         * 若追求快捷开发，用更优雅的写法，可以选择本插件
         * 如果是追求性能，更倾向于清晰的，显式的代码结构，可以遵循原生写法
         */
    }


    @Translation(name = "res_code_to_msg")
    @Getter
    public static enum CodeEnum {

        SUCCESS("200", "成功"),
        ERROR("500", "失败"),
        ;


        @TransMapper
        private String code;

        @TransValue
        private String msg;

        CodeEnum(String code, String msg) {
            this.code = code;
            this.msg = msg;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class User {
        private Integer id;

        private String username;

        private String nickName;
    }

    public static class UserService {
        Map<Integer, User> userFactory = new ConcurrentHashMap<>();

        @Translation(name = "current_time")
        public static String getCurrentTime() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return simpleDateFormat.format(new Date());
        }

        @Translation(name = "user_id_to_user")
        public User getUserById(Integer id) {
            try {
                Thread.sleep(6); //模仿IO操作
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return userFactory.computeIfAbsent(id, k -> new User(id, "username" + id, "nickName" + id));
        }
    }
}
