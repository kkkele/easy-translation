package com.superkele.translation.test.processor;

import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.annotation.TransMapper;
import com.superkele.translation.annotation.TransValue;
import com.superkele.translation.annotation.Translation;
import com.superkele.translation.core.context.support.DefaultTransExecutorContext;
import com.superkele.translation.test.context.DefaultTransExecutorContextTest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class DefaultTransProcessorTest {

    DefaultTransExecutorContextTest.UserService userService = new DefaultTransExecutorContextTest.UserService();
    DefaultTransExecutorContext context = DefaultTransExecutorContext.builder()
            .invokeObjs(userService)
            .packages("com.superkele.translation.test.processor")
            .build();

    @Test
    public void commonTest() {
        Operate operate = new Operate();
        operate.setOperateId(1);
        operate.setOperateTime("增加");
        operate.setActionCode(CodeEnum.SUCCESS.code);
        operate.setUserId(1);
    }

    @Translation(name = "res_code_to_msg")
    @Getter
    public enum CodeEnum {

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
    public static class Operate {
        private Integer OperateId;

        private String actionName;

        private String actionCode;

        @Mapping(translator = "res_code_to_msg", mapper = "actionCode")
        private String actionDesc;

        private Integer userId;

        @Mapping(translator = "user_id_to_user", mapper = "userId", receive = "username")
        private String username;

        @Mapping(translator = "current_time")
        private String operateTime;
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
        Map<Integer, DefaultTransExecutorContextTest.User> userFactory = Map.of(1, new DefaultTransExecutorContextTest.User(1, "superkele", "小牛"),
                2, new DefaultTransExecutorContextTest.User(2, "admin", "小明"),
                3, new DefaultTransExecutorContextTest.User(3, "root", "小王"));

        @Translation(name = "current_time")
        public static String getCurrentTime() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return simpleDateFormat.format(new Date());
        }

        @Translation(name = "user_id_to_user")
        public DefaultTransExecutorContextTest.User getUserById(Integer id) {
            try {
                Thread.sleep(6); //模仿IO操作
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return userFactory.get(id);
        }
    }
}
