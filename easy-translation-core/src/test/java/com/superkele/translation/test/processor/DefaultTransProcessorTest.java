package com.superkele.translation.test.processor;

import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.annotation.TransMapper;
import com.superkele.translation.annotation.TransValue;
import com.superkele.translation.annotation.Translation;
import com.superkele.translation.core.annotation.MappingHandler;
import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.context.TransExecutorContext;
import com.superkele.translation.core.context.support.DefaultTransExecutorContext;
import com.superkele.translation.core.processor.support.DefaultTranslationProcessor;
import com.superkele.translation.test.context.DefaultTransExecutorContextTest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Executors;

public class DefaultTransProcessorTest {

    DefaultTransExecutorContextTest.UserService userService = new DefaultTransExecutorContextTest.UserService();
    DefaultTransExecutorContext context = DefaultTransExecutorContext.builder()
            .invokeObjs(userService)
            .packages("com.superkele.translation.test.processor")
            .config(new Config()
                    .setThreadPoolExecutor(Executors.newFixedThreadPool(32)))
            .build();
    DefaultTranslationProcessor processor = new LoggerTranslationProcessor(context);

    @Test
    public void commonTest() {
        SyncOperate syncOperate = new SyncOperate();
        syncOperate.setOperateId(1);
        syncOperate.setOperateName("测试");
        syncOperate.setOperateTime("增加");
        syncOperate.setResCode(CodeEnum.SUCCESS.code);
        syncOperate.setUserId(1);
        processor.process(syncOperate);
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
    public static class SyncOperate {
        private Integer OperateId;

        private String operateName;

        private String resCode;

        @Mapping(translator = "res_code_to_msg", mapper = "resCode")
        private String resMsg;

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

    public static class LoggerTranslationProcessor extends DefaultTranslationProcessor{

        public LoggerTranslationProcessor(TransExecutorContext context) {
            super(context);
        }

        public LoggerTranslationProcessor(TransExecutorContext context, MappingHandler mappingHandler) {
            super(context, mappingHandler);
        }

        @Override
        public void process(Object obj) {
            System.out.println("before:" + obj);
            System.out.println("processing~~~~~~~~~~~~~");
            super.process(obj);
            System.out.println("after:" + obj);
        }
    }
}
