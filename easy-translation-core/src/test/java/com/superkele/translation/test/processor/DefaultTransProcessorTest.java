package com.superkele.translation.test.processor;

import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.annotation.TransMapper;
import com.superkele.translation.annotation.TransValue;
import com.superkele.translation.annotation.Translation;
import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.context.TransExecutorContext;
import com.superkele.translation.core.context.support.DefaultTransExecutorContext;
import com.superkele.translation.core.processor.support.DefaultTranslationProcessor;
import com.superkele.translation.test.util.TimeRecorder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

public class DefaultTransProcessorTest {

    UserService userService = new UserService();
    ShopService shopService = new ShopService();
    DefaultTransExecutorContext context = DefaultTransExecutorContext.builder()
            .invokeObjs(userService, shopService)
            .packages("com.superkele.translation.test.processor")
            .config(new Config()
                    .setThreadPoolExecutor(Executors.newFixedThreadPool(32)))
            .build();
    DefaultTranslationProcessor loggerProcessor = new LoggerTranslationProcessor(context);
    DefaultTranslationProcessor processor = new DefaultTranslationProcessor(context);

    /**
     * 翻译字段全sync基础测试
     */
    @Test
    public void commonTest() {
        SyncOperate syncOperate = new SyncOperate();
        syncOperate.setOperateId(1);
        syncOperate.setOperateName("测试");
        syncOperate.setOperateTime("增加");
        syncOperate.setResCode(CodeEnum.SUCCESS.code);
        syncOperate.setUserId(1);
        loggerProcessor.process(syncOperate);
    }

    /**
     * 翻译字段全sync性能测试
     */
    /**
     * 循环一千次的结果大体如下
     * [0]
     * origin cost7561ms
     * translate cost7620ms
     * [1]
     * origin cost7182ms
     * translate cost7242ms
     * [2]
     * origin cost7257ms
     * translate cost7264ms
     * [3]
     * origin cost7152ms
     * translate cost7196ms
     * 这些性能消耗，大家可以自行抉择
     */
    @Test
    public void performanceTest() {
        //预热
        for (int i = 0; i < 20; i++) {
            SyncOperate syncOperate = new SyncOperate();
            syncOperate.setOperateId(1);
            syncOperate.setOperateName("测试");
            syncOperate.setOperateTime("增加");
            syncOperate.setResCode(CodeEnum.SUCCESS.code);
            syncOperate.setUserId(1);
            processor.process(syncOperate);
        }
        //测试
        CompletableFuture[] completableFutures = new CompletableFuture[2];
        int repeatTimes = 1000;
        completableFutures[0] = CompletableFuture.runAsync(() -> {
            long record = TimeRecorder.record(() -> {
                SyncOperate syncOperate = new SyncOperate();
                syncOperate.setOperateId(1);
                syncOperate.setOperateName("测试");
                syncOperate.setOperateTime(UserService.getCurrentTime());
                syncOperate.setResCode(CodeEnum.SUCCESS.code);
                syncOperate.setResMsg(CodeEnum.getMsgByCode(syncOperate.getResCode()));
                syncOperate.setUserId(1);
                syncOperate.setUsername(Optional
                        .ofNullable(userService.getUserById(syncOperate.getUserId()))
                        .map(User::getUsername)
                        .get());
            }, repeatTimes);
            System.out.println("origin cost" + record + "ms");
        });
        completableFutures[1] = CompletableFuture.runAsync(() -> {
            long record = TimeRecorder.record(() -> {
                SyncOperate syncOperate = new SyncOperate();
                syncOperate.setOperateId(1);
                syncOperate.setOperateName("测试");
                syncOperate.setResCode(CodeEnum.SUCCESS.code);
                syncOperate.setUserId(1);
                processor.process(syncOperate);
            }, repeatTimes);
            System.out.println("translate cost" + record + "ms");
        });
        CompletableFuture.allOf(completableFutures).join();
    }


    /**
     * 翻译字段全Async性能测试
     */
    @Test
    public void asyncTest() {
        //预热
        for (int i = 0; i < 1000; i++) {
            AsyncOperate operate = new AsyncOperate();
            operate.setOperateId(1);
            operate.setOperateName("测试");
            operate.setUserId(1);
            processor.process(operate);
        }
        //测试
        CompletableFuture[] completableFutures = new CompletableFuture[2];
        int repeatTimes = 1000;
        completableFutures[0] = CompletableFuture.runAsync(() -> {
            long record = TimeRecorder.record(() -> {
                AsyncOperate operate = new AsyncOperate();
                operate.setOperateId(1);
                operate.setOperateName("测试");
                operate.setShopId(1);
                operate.setShopName(Optional
                        .ofNullable(shopService.getShopById(operate.getShopId()))
                        .map(DefaultTransProcessorTest.Shop::getShopName)
                        .get());
                operate.setUserId(1);
                operate.setUsername(Optional
                        .ofNullable(userService.getUserById(operate.getUserId()))
                        .map(DefaultTransProcessorTest.User::getUsername)
                        .get());
            }, repeatTimes);
            System.out.println("origin cost" + record + "ms");
        });
        completableFutures[1] = CompletableFuture.runAsync(() -> {
            long record = TimeRecorder.record(() -> {
                AsyncOperate operate = new AsyncOperate();
                operate.setOperateId(1);
                operate.setOperateName("测试");
                operate.setUserId(1);
                operate.setShopId(1);
                processor.process(operate);
            }, repeatTimes);
            System.out.println("translate cost" + record + "ms");
        });
        CompletableFuture.allOf(completableFutures).join();
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

        public static String getMsgByCode(String code) {
            for (CodeEnum value : CodeEnum.values()) {
                if (value.code.equals(code)) {
                    return value.msg;
                }
            }
            return null;
        }
    }

    @Data
    @AllArgsConstructor
    public static class Shop {
        private Integer shopId;
        private String shopName;
    }


    @Data
    public static class ShopService {
        Map<Integer, Shop> shopMap = new ConcurrentHashMap<>();

        @Translation(name = "shop_id_to_shop")
        public Shop getShopById(Integer shopId) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return Optional.ofNullable(shopId).map(key -> shopMap.computeIfAbsent(key, k -> new Shop(k, "shop" + k))).orElse(null);
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
    public static class AsyncOperate {
        private Integer OperateId;

        private String operateName;

        private Integer userId;

        @Mapping(translator = "user_id_to_user", mapper = "userId", receive = "username", async = true)
        private String username;

        private Integer shopId;

        @Mapping(translator = "shop_id_to_shop", mapper = "shopId", receive = "shopName", async = true)
        private String shopName;

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
        ;

        @Translation(name = "current_time")
        public static String getCurrentTime() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return simpleDateFormat.format(new Date());
        }

        @Translation(name = "user_id_to_user")
        public User getUserById(Integer id) {
            try {
                Thread.sleep(1); //模仿IO操作
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return Optional.ofNullable(id).map(key -> userFactory.computeIfAbsent(key, k -> new User(k, "user" + k, "nick" + k))).orElse(null);
        }
    }

    public static class LoggerTranslationProcessor extends DefaultTranslationProcessor {

        public LoggerTranslationProcessor(TransExecutorContext context) {
            super(context);
        }


        @Override
        public void process(Object obj) {
            long begin = System.currentTimeMillis();
            System.out.println("before:" + obj);
            System.out.println("processing~~~~~~~~~~~~~");
            super.process(obj);
            System.out.println("after:" + obj);
            long end = System.currentTimeMillis();
            System.out.println("cost : " + (end - begin) + "ms");
        }
    }
}
