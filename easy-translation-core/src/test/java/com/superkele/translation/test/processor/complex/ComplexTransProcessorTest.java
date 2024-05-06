package com.superkele.translation.test.processor.complex;

import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.context.support.DefaultTransExecutorContext;
import com.superkele.translation.core.processor.support.DefaultTranslationProcessor;
import com.superkele.translation.test.processor.entity.Operate;
import com.superkele.translation.test.processor.entity.Shop;
import com.superkele.translation.test.processor.entity.User;
import com.superkele.translation.test.processor.service.OperateService;
import com.superkele.translation.test.processor.service.ShopService;
import com.superkele.translation.test.processor.service.UserService;
import com.superkele.translation.test.util.TimeRecorder;
import lombok.Data;
import org.junit.Test;

import java.util.concurrent.Executors;


public class ComplexTransProcessorTest {

    UserService userService = new UserService();
    OperateService operateService = new OperateService();
    ShopService shopService = new ShopService();
    DefaultTransExecutorContext context = DefaultTransExecutorContext.builder()
            .invokeObjs(userService, shopService, operateService)
            .packages("com.superkele.translation.test.processor.complex")
            .build();
    DefaultTransExecutorContext asyncContext = DefaultTransExecutorContext.builder()
            .invokeObjs(userService, shopService, operateService)
            .packages("com.superkele.translation.test.processor.complex")
            .config(new Config().setThreadPoolExecutor(Executors.newCachedThreadPool()))
            .build();
    DefaultTranslationProcessor asyncProcessor = new DefaultTranslationProcessor(asyncContext);
    DefaultTranslationProcessor processor = new DefaultTranslationProcessor(context);


    @Test
    public void commonTest() {
        ComplexOperateVO complexOperateVO = new ComplexOperateVO();
        complexOperateVO.setOperateId(1);
        processor.process(complexOperateVO);
        System.out.println(complexOperateVO);
    }

    /**
     * 在充分预热之后，执行结果如下，性能消耗微乎其微
     * [0]
     syncMethod cost =2258ms

     processor cost =2352ms
     <hr>
     * [1]
     syncMethod cost =2261ms

     processor cost =2368ms
     <hr>
     * [2]
     syncMethod cost =2276ms

     processor cost =2348ms
     */
    @Test
    public void test() {
        //jvm预热
        TimeRecorder.record(() -> {
            ComplexOperateVO complexOperateVO = new ComplexOperateVO();
            complexOperateVO.setOperateId(1);
            processor.process(complexOperateVO);
        }, 20);
        TimeRecorder.record(() -> {
            ComplexOperateVO complexOperateVO = new ComplexOperateVO();
            complexOperateVO.setOperateId(1);
            Operate operate = operateService.getOperate(complexOperateVO.operateId);
            complexOperateVO.operateName = operate.getName();
            complexOperateVO.operateDesc = operateService.convertOperateName(complexOperateVO.operateName);
            complexOperateVO.userId = operate.getUserId();
            User user = userService.getUser(complexOperateVO.userId);
            complexOperateVO.username = user.getUsername();
            complexOperateVO.shopId = user.getShopId();
            Shop shop = shopService.getShop(complexOperateVO.shopId);
            complexOperateVO.shopName = shop.getShopName();
        }, 20);
        //正式测试
        long record2 = TimeRecorder.record(() -> {
            ComplexOperateVO complexOperateVO = new ComplexOperateVO();
            complexOperateVO.setOperateId(1);
            Operate operate = operateService.getOperate(complexOperateVO.operateId);
            complexOperateVO.operateName = operate.getName();
            complexOperateVO.operateDesc = operateService.convertOperateName(complexOperateVO.operateName);
            complexOperateVO.userId = operate.getUserId();
            User user = userService.getUser(complexOperateVO.userId);
            complexOperateVO.username = user.getUsername();
            complexOperateVO.shopId = user.getShopId();
            Shop shop = shopService.getShop(complexOperateVO.shopId);
            complexOperateVO.shopName = shop.getShopName();
            if (complexOperateVO.getShopName() == null){
                throw new RuntimeException("赋值失败");
            }
        }, 100);
        System.out.println("syncMethod cost =" + record2 + "ms"+ """
                    
                    """);
        long record = TimeRecorder.record(() -> {
            ComplexOperateVO complexOperateVO = new ComplexOperateVO();
            complexOperateVO.setOperateId(1);
            processor.process(complexOperateVO);
            if (complexOperateVO.getShopName() == null){
                throw new RuntimeException("赋值失败");
            }
        }, 100);
        System.out.println("processor cost =" + record + "ms");
    }

    /**
     * 开启多线程处理之后，结果如下,具体的就不和普通写法做对比了，大家自行判断
     * processor cost =1832ms
     * processor cost =1804ms
     * processor cost =1794ms
     * processor cost =1785ms
     * processor cost =1769ms
     * processor cost =1748ms
     * processor cost =1780ms
     * processor cost =1754ms
     * processor cost =1749ms
     */
    @Test
    public void asyncTest() {
        //预热
        for (int i = 0; i < 20; i++){
            ComplexOperateVO complexOperateVO = new ComplexOperateVO();
            complexOperateVO.setOperateId(1);
            asyncProcessor.process(complexOperateVO);
        }
        //正式执行
        while (true) {
            long record = TimeRecorder.record(() -> {
                ComplexOperateVO complexOperateVO = new ComplexOperateVO();
                complexOperateVO.setOperateId(1);
                asyncProcessor.process(complexOperateVO);
            }, 100);
            System.out.println("processor cost =" + record + "ms");
        }
    }

    /**
     * userId先查，在获取userId对应的shop，然后渲染出shopName
     * 同时，并发的执行其他操作
     */
    @Data
    public static class ComplexOperateVO {

        private Integer operateId;

        @Mapping(translator = "operate_id_to_operate", mapper = "operateId", sort = 0, receive = "name")
        private String operateName;

        @Mapping(translator = "operate_name_to_desc", mapper = "operateName", after = "operateName", sort = 1, async = true)
        private String operateDesc;

        @Mapping(translator = "operate_id_to_operate", mapper = "operateId", sort = 0, receive = "userId")
        private Integer userId;

        @Mapping(translator = "user_id_to_user", mapper = "userId", receive = "username", after = "userId", async = true)
        private String username;

        @Mapping(translator = "user_id_to_user", mapper = "userId", receive = "shopId", after = "userId", async = true)
        private Integer shopId;

        @Mapping(translator = "shop_id_to_shop", mapper = "shopId", receive = "shopName", after = "shopId", async = true)
        private String shopName;
    }
}
