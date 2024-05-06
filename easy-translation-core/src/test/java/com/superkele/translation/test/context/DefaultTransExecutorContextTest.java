package com.superkele.translation.test.context;

import com.superkele.translation.annotation.TransMapper;
import com.superkele.translation.annotation.TransValue;
import com.superkele.translation.annotation.Translation;
import com.superkele.translation.core.context.support.DefaultTransExecutorContext;
import com.superkele.translation.core.translator.handle.TranslateExecutor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

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
        Map<Integer, User> userFactory = Map.of(1, new User(1, "superkele", "小牛"),
                2, new User(2, "admin", "小明"),
                3, new User(3, "root", "小王"));

        @Translation(name = "current_time")
        public static String getCurrentTime() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return simpleDateFormat.format(new Date());
        }

        @Translation(name = "user_id_to_user")
        public User getUserById(Integer id) {
            return userFactory.get(id);
        }
    }
}
