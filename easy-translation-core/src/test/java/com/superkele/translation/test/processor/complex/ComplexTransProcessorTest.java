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
            .config(new Config().setThreadPoolExecutor(Executors.newFixedThreadPool(10)))
            .build();
    DefaultTranslationProcessor processor = new DefaultTranslationProcessor(context);


    @Test
    public void commonTest() {
        ComplexOperateVO complexOperateVO = new ComplexOperateVO();
        complexOperateVO.setOperateId(1);
        processor.process(complexOperateVO);
        System.out.println(complexOperateVO);
    }

    @Test
    public void test() {
        //jvm预热
        TimeRecorder.record(() -> {
            ComplexOperateVO complexOperateVO = new ComplexOperateVO();
            complexOperateVO.setOperateId(1);
            processor.process(complexOperateVO);
        }, 10);
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
        }, 10);
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
        }, 500);
        System.out.println("syncMethod cost =" + record2 + "ms");
        long record = TimeRecorder.record(() -> {
            ComplexOperateVO complexOperateVO = new ComplexOperateVO();
            complexOperateVO.setOperateId(1);
            processor.process(complexOperateVO);
            if (complexOperateVO.getShopName() == null){
                throw new RuntimeException("赋值失败");
            }
        }, 500);
        System.out.println("asyncMethod cost =" + record + "ms");
    }

    @Test
    public void test3() {
        int i =0;
        while (true) {
            System.out.println("第" + (++i) + "次");
            test();
        }
    }

    /**
     * userId先查，在获取userId对应的shop，然后渲染出shopName
     * 同时，并发的执行其他操作
     */
    @Data
    public static class ComplexOperateVO {

        private Integer operateId;

        @Mapping(translator = "operate_id_to_operate", mapper = "operateId", sort = 0, receive = "name", async = true)
        private String operateName;

        @Mapping(translator = "operate_name_to_desc", mapper = "operateName", after = "operateName", sort = 1, async = true)
        private String operateDesc;

        @Mapping(translator = "operate_id_to_operate", mapper = "operateId", sort = 0, receive = "userId", async = true)
        private Integer userId;

        @Mapping(translator = "user_id_to_user", mapper = "userId", receive = "username", after = "userId", async = true)
        private String username;

        @Mapping(translator = "user_id_to_user", mapper = "userId", receive = "shopId", after = "userId", async = true)
        private Integer shopId;

        @Mapping(translator = "shop_id_to_shop", mapper = "shopId", receive = "shopName", after = "shopId", async = true)
        private String shopName;
    }
}
