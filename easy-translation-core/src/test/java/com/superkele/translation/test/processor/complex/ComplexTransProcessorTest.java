package com.superkele.translation.test.processor.complex;

import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.context.support.DefaultTransExecutorContext;
import com.superkele.translation.core.processor.support.DefaultTranslationProcessor;
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

/*        long record2 = TimeRecorder.record(() -> {
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
        }, 1000);
        System.out.println("syncMethod cost =" + record2 + "ms");*/
        long record = TimeRecorder.record(() -> {
            ComplexOperateVO complexOperateVO = new ComplexOperateVO();
            complexOperateVO.setOperateId(1);
            processor.process(complexOperateVO);
            System.out.println(complexOperateVO);
            if (complexOperateVO.getShopName() == null){
                throw new RuntimeException("执行有误");
            }
        }, 1);
        System.out.println("translatorProcessor cost =" + record + "ms");

    }

    @Test
    public void test3(){
        for (int i = 1; i <= 10000; i++) {
            System.out.println("第"+i +"次");
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

        @Mapping(translator = "operate_id_to_operate", mapper = "operateId", sort = 0, receive = "name")
        private String operateName;

        @Mapping(translator = "operate_name_to_desc", mapper = "operateName", sort = 1)
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
