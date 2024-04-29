package com.superkele.translation.test.processor;

import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.annotation.Translation;
import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.context.TransExecutorContext;
import com.superkele.translation.core.context.support.DefaultTransExecutorContext;
import com.superkele.translation.core.handler.support.CacheableTranslationProcessor;
import com.superkele.translation.core.translator.Translator;
import com.superkele.translation.core.translator.handle.TranslateExecutor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

public class ProcessorTest {

    UserService userService = new UserService();
    static Config config = new Config();
    static{
        config.registerTranslatorClazz(ThreeParam.class);
    }
    DefaultTransExecutorContext context = new DefaultTransExecutorContext(config,new Object[]{userService},new String[]{"com.superkele.translation"});
    CacheableTranslationProcessor processor = new CacheableTranslationProcessor() {
        @Override
        public void process(Object obj, ThreadPoolExecutor executor) {
        }

        @Override
        public void process(List<Object> obj, ThreadPoolExecutor executor) {
        }

        @Override
        public void processAsync(Object obj) {
        }

        @Override
        public void processAsync(List<Object> obj) {
        }

        @Override
        protected TransExecutorContext getContext() {
            return context;
        }
    };

    @Test
    public void test() {
        Order source = new Order("123", 1,1);
        System.out.println(source);
        processor.process(source);
        System.out.println(source);
    }


    public interface ThreeParam extends Translator {
        Object translate(Object var0, Object var1, Object var2);

        @Override
        default TranslateExecutor getDefaultExecutor() {
            return args -> translate(args[0], args[1], args[2]);
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

        @Mapping(translator = "getUserNameByIdV2", mapper = {"createBy", "type"}, other = {"HELLO WORLD","NICE "})
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
        public String getUserNameById(Integer id, Integer type, String other) {
            if (type != null && type == 0){
                return "default Value";
            }
            return map.get(id).getName() + other;
        }
    }
}
