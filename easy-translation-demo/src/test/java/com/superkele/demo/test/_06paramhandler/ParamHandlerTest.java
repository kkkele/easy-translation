package com.superkele.demo.test._06paramhandler;

import cn.hutool.core.collection.ListUtil;
import com.superkele.demo.EasyTranslationDemoApplication;
import com.superkele.demo.paramhandler.Animal;
import com.superkele.demo.paramhandler.Animal2;
import com.superkele.demo.paramhandler.Animal3;
import com.superkele.translation.annotation.constant.DefaultUnpackingHandler;
import com.superkele.translation.core.config.TranslationAutoConfigurationCustomizer;
import com.superkele.translation.core.processor.TranslationProcessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@SpringBootTest(classes = {ParamHandlerTest.TestConfig.class, EasyTranslationDemoApplication.class})
@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
public class ParamHandlerTest {

    @Autowired
    private TranslationProcessor translationProcessor;

    /**
     * 测试单参数映射，是否能成功生效
     *
     * @see com.superkele.demo.paramhandler.Animal
     * @see com.superkele.demo.paramhandler.IntToStringParamHandler
     */
    @Test
    public void test_single_param_handler() {
        Animal animal = new Animal();
        animal.setCode1(1);
        animal.setCode2(2);
        translationProcessor.process(animal);
        Assert.assertNotNull(animal.getName1());
        Assert.assertNotNull(animal.getName2());
    }

    /**
     * 测试多参数映射时，是否能够生效
     *
     * @see com.superkele.demo.paramhandler.Animal2
     * @see com.superkele.demo.paramhandler.StringToListParamHandler
     */
    @Test
    public void test_multi_param_handler() {
        Animal2 animal = new Animal2();
        animal.setVar1("1");
        animal.setVar2("2");
        translationProcessor.process(animal);
        Assert.assertNotNull(animal.getVar3());
        Assert.assertNotNull(animal.getVar4());
    }

    /**
     * 测试 批量映射时，参数能否正确传递
     */
    @Test
    public void test_batch_param_handler() {
        List<Animal3> animal3s = ListUtil.of(new Animal3(), new Animal3(), new Animal3(), new Animal3());
        translationProcessor.process(animal3s, Animal3.class, "", false, DefaultUnpackingHandler.class);
        animal3s.forEach(animal3 -> {
            //   Assert.assertNotNull(animal3.getTypeName1());
            Assert.assertNotNull(animal3.getTypeName2());
            Assert.assertNotNull(animal3.getTypeName3());
            Assert.assertNotNull(animal3.getTypeName4());
        });
    }

    /**
     * 关闭缓存功能，防止结果复用
     */
    @Configuration
    public static class TestConfig {

        @Bean
        public TranslationAutoConfigurationCustomizer translationAutoConfigurationCustomizer2() {
            return config -> config.setCacheEnabled(() -> false);
        }
    }
}
