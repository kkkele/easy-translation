package com.superkele.translation.test.processor;

import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.annotation.Translation;
import com.superkele.translation.core.context.TransExecutorContext;
import com.superkele.translation.core.context.support.DefaultTransExecutorContext;
import com.superkele.translation.core.handler.support.CacheableTranslationProcessor;
import com.superkele.translation.test.method.LoginUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

public class ProcessorTest {

    @Test
    public void test() {
        UserService userService = new UserService();
        DefaultTransExecutorContext context = new DefaultTransExecutorContext(userService);
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
        Order source = new Order("123", 1);
        processor.process(source);
        System.out.println(source);
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

        @Mapping(translator = "getUserById", mapper = "createBy", receive = "name")
        private String createName;

        @Mapping(translator = "getUserNameByIdV2", mapper = "createBy", other = "xx")
        private  String otherName;

        public Order(String orderNo, Integer createBy) {
            this.orderNo = orderNo;
            this.createBy = createBy;
        }
    }

    public static class UserService {

        Map<Integer, User> map = Map.of(1, new User(1, "黄绿灯"),
                2, new User(2, "红绿灯"),
                3, new User(3, "红黄灯"));

        @Translation(name = "getUserById")
        public User getUser(Integer id) {
            return map.get(id);
        }

        @Translation(name = "getUserNameByIdV2")
        public String getUserById(Integer id, String other) {
            return map.get(id) + other;
        }
    }
}
