package com.superkele.demo.test._0_context;


import com.superkele.demo.context.Status;
import com.superkele.demo.context.User;
import com.superkele.demo.test.utils.PropertyUtils;
import com.superkele.translation.core.context.support.DefaultTranslatorContext;
import com.superkele.translation.core.translator.Translator;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * 翻译器 上下文获取
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
public class TranslatorContextTest {

    @Autowired
    private DefaultTranslatorContext translatorContext;


    /**
     * 测试静态方法创建的translator （无需invokeBean激活，单例的翻译器）
     *
     * @see com.superkele.demo.context.AppUserService#produceRandomUser(Integer)
     */
    @Test
    public void testStaticTranslator() {
        Translator translator = translatorContext.findTranslator("produceRandomUser");
        User user = (User) translator.doTranslate(1);
        PropertyUtils.getProperties(user).forEach((key, v) -> {
            Assert.assertNotNull(v);
        });
    }

    /**
     * 测试 单例的动态方法（只有第一次加载的时候会创建，只从beanFactory中获取一次invokeBean，然后一直复用该translator）
     *
     * @see com.superkele.demo.context.AppUserService#getUserByIdSingleton(Integer)
     */
    @Test
    public void testSingletonDynamicTranslator() {
        Translator translator1 = translatorContext.findTranslator("getUserByIdSingleton");
        User user = (User) translator1.doTranslate(1);
        PropertyUtils.getProperties(user).forEach((key, v) -> {
            Assert.assertNotNull(v);
        });
        Translator translator2 = translatorContext.findTranslator("getUserByIdSingleton");
        //查看翻译器是否为单例Bean
        assert translator1 == translator2;
    }

    /**
     * 测试 原型的动态方法 （每次调用translator会重新创建，从beanFactory中重新获取bean）
     *
     * @see com.superkele.demo.context.AppUserService#getUserByIdPrototype(Integer)
     */
    @Test
    public void testPrototypeDynamicTranslator() {
        Translator translator1 = translatorContext.findTranslator("getUserByIdPrototype");
        User user = (User) translator1.doTranslate(1);
        PropertyUtils.getProperties(user).forEach((key, v) -> {
            Assert.assertNotNull(v);
        });
        Translator translator2 = translatorContext.findTranslator("getUserByIdPrototype");
        log.info("translator1:{} translator2:{}", translator1, translator2);
        //查看翻译器是否为原型例Bean
        assert translator1 != translator2;
    }

    /**
     * 测试 将translator标记在接口上，然后动态查找翻译器实现
     *
     * @see com.superkele.demo.context.AppUserService#getUserById(Integer)
     * @see com.superkele.demo.context.AppUserService#getUserById2(Integer)
     */
    @Test
    public void testInterfaceTranslator() {
        Translator translator = translatorContext.findTranslator("getUserById");
        User user = (User) translator.doTranslate(1);
        PropertyUtils.getProperties(user).forEach((key, v) -> {
            Assert.assertNotNull(v);
        });
        Translator translator2 = translatorContext.findTranslator("getUserById2");
        user = (User) translator2.doTranslate(1);
        PropertyUtils.getProperties(user).forEach((key, v) -> {
            Assert.assertNotNull(v);
        });
    }


    /**
     * 测试 使用enum创建的translator
     *
     * @see com.superkele.demo.context.Status
     */
    @Test
    public void testEnumTranslator() {
        Translator translator = translatorContext.findTranslator("getStatus");
        Assert.assertEquals(Status.NORMAL.getDesc(), translator.doTranslate(0));
        Assert.assertEquals(Status.DISABLED.getDesc(), translator.doTranslate(1));
    }

}
