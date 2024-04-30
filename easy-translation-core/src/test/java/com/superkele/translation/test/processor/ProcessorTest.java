package com.superkele.translation.test.processor;

import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.annotation.TransMapper;
import com.superkele.translation.annotation.Translation;
import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.context.TransExecutorContext;
import com.superkele.translation.core.context.support.DefaultTransExecutorContext;
import com.superkele.translation.core.handler.support.CacheableTranslationProcessor;
import com.superkele.translation.core.metadata.FieldInfo;
import com.superkele.translation.core.translator.Translator;
import com.superkele.translation.core.translator.handle.TranslateExecutor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class ProcessorTest {

    static Config config = new Config();

    static {
        config.registerTranslatorClazz(ThreeParam.class);
    }

    UserService userService = new UserService();
    DefaultTransExecutorContext context = new DefaultTransExecutorContext(config, new Object[]{userService}, new String[]{"com.superkele.translation"});

    @Test
    public void test() {
        TranslateExecutor executor = context.findExecutor("getUserNameByIdV3");
        System.out.println(executor.execute("hello,","world",666,"yyyyyyyyy"));
    }


    public interface ThreeParam extends Translator {
        Object translate(Object var0, Object var1, Object var2);

        @Override
        default Object doTranslate(Object... args) {
            return translate(args[0], args[1], args[2]);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class User {

        private Integer id;


        private String name;
    }

    @Data
    public static class Order {
        private String orderNo;

        private Integer createBy;

        private Integer type;

        @Mapping(translator = "getUserById", mapper = "createBy", receive = "name")
        private String createName;

        @Mapping(translator = "getUserNameByIdV2", mapper = {"createBy", "type"}, other = {"HELLO WORLD", "NICE "})
        private String otherName;

        public Order(String orderNo, Integer createBy, Integer type) {
            this.orderNo = orderNo;
            this.createBy = createBy;
            this.type = type;
        }
    }

    public static class UserService {

        Map<Integer, User> map = Map.of(
                1, new User(1, "黄绿灯"),
                2, new User(2, "红绿灯"),
                3, new User(3, "红黄灯"));

        @Translation(name = "getUserById")
        public User getUser(Integer id) {
            return map.get(id);
        }

        @Translation(name = "getUserNameByIdV2")
        public String getUserNameById(Integer id, @TransMapper Integer type, String other) {
            if (type != null && type == 0) {
                return "default Value";
            }
            return map.get(id).getName() + other;
        }


        @Translation(name = "getUserNameByIdV3")
        public String getUserNameById(Integer id, @TransMapper String type, @TransMapper String other) {
            return type + other + id;
        }
    }
}
